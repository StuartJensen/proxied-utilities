package com.pp.proxied.utilities;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.extra.paid.ExtraPaidManager;
import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.register.Register;
import com.pp.proxied.utilities.register.RegisterParser;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.reporting.BalancesReport;
import com.pp.proxied.utilities.reporting.CashFlowReport;
import com.pp.proxied.utilities.reporting.CoverageReport;
import com.pp.proxied.utilities.reporting.LedgerReport;
import com.pp.proxied.utilities.reporting.PaymentsReport;
import com.pp.proxied.utilities.reporting.RegisterReport;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class ProxiedUtilities
{
	private static final int ARGS_MIN_COUNT = 4;
	private static final int ARGS_MAX_COUNT = 10;
	
	private static final String ARG_TAG_INFILE = "-inFile";
	private static final String ARG_TAG_OUTFILE = "-outFile";
	private static final String ARG_TAG_REPORT = "-report";
	private static final String ARG_TAG_TARGET = "-target";
	
	private static final String ARG_REPORT_TYPE_REGISTER = "register";
	private static final String ARG_REPORT_TYPE_LEDGER = "ledger";
	private static final String ARG_REPORT_TYPE_PAYMENTS = "payments";
	private static final String ARG_REPORT_TYPE_BALANCES = "balances";
	private static final String ARG_REPORT_TYPE_COVERAGE = "coverage";
	private static final String ARG_REPORT_TYPE_EXTRA = "extra";
	private static final String ARG_REPORT_TYPE_CASHFLOW = "cashflow";
	
	private static List<String> REPORT_TYPES;
	private static List<String> REPORT_DESCRIPTIONS;
	
	static
	{
		REPORT_TYPES = new ArrayList<String>();
		REPORT_TYPES.add(ARG_REPORT_TYPE_REGISTER);
		REPORT_TYPES.add(ARG_REPORT_TYPE_LEDGER);
		REPORT_TYPES.add(ARG_REPORT_TYPE_PAYMENTS);
		REPORT_TYPES.add(ARG_REPORT_TYPE_BALANCES);
		REPORT_TYPES.add(ARG_REPORT_TYPE_COVERAGE);
		REPORT_TYPES.add(ARG_REPORT_TYPE_EXTRA);
		REPORT_TYPES.add(ARG_REPORT_TYPE_CASHFLOW);
		REPORT_DESCRIPTIONS = new ArrayList<String>();
		REPORT_DESCRIPTIONS.add("Details the register entries. May be filtered using a tenant or payee name as the target.");
		REPORT_DESCRIPTIONS.add("Details the ledger entries. May be filtered using a tenant or payee name as the target.");
		REPORT_DESCRIPTIONS.add("Details all active payments in the ledger.");
		REPORT_DESCRIPTIONS.add("Details per tenant account balances for each ledger entry.");
		REPORT_DESCRIPTIONS.add("Details payee service time coverage (1 is desirable) and total active tenants coverage.");
		REPORT_DESCRIPTIONS.add("Details how many extra cents each tenant has paid. Daily payments can have mathmatical rounding issues such that one tenant must pay one cent extra. This extra payment should be rotated fairly among tenants.");
		REPORT_DESCRIPTIONS.add("Details yearly ledger cash flow: Payments vs. Deposits.");
	};
	
	private static String m_strReportType = null;
	private static String m_strTarget = null;
	private static File m_fOutputFile = null;
	private static File m_fInputFile = null;
	
	public static SimpleDateFormat DAYHOUR_DATEFORMAT = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss");
	
	private static List<RegisterBaseEntry> m_lEntries;
	
	public static void main(String[] args)
	{
		if (args.length < ARGS_MIN_COUNT)
		{
			showUsage("Too few arguments");
			System.exit(1);
		}
		if (args.length > ARGS_MAX_COUNT)
		{
			showUsage("Too many arguments");
			System.exit(2);
		}
		
		processArguments(args);
		
		StringBuilder sb = new StringBuilder(2048);
		try
		{
			RegisterParser parser = new RegisterParser(m_fInputFile);
			m_lEntries = parser.parse();
			
			if ((null != m_lEntries) && (!m_lEntries.isEmpty()))
			{
				Register register = new Register(m_lEntries);
				Ledger ledger = new Ledger(register);
				
				// Resolve target if there is one specified on the command line
				RegisterBaseEntry target = null;
				if (StringUtil.isDefined(m_strTarget))
				{
					target = resolveTarget(register, m_strTarget); 
					if (null == target)
					{
						throw new IllegalStateException("Unresolvable target: " + m_strTarget + ". Neither a tenant nor a payee");
					}
				}
				
				Calendar today = Calendar.getInstance();
				String strReportGenerationTime = DateUtil.getTime(DAYHOUR_DATEFORMAT, today.getTime().getTime());
				sb.append(m_strReportType.toUpperCase()).append(" Report Generated on ").append(strReportGenerationTime).append("\n");
				if (m_strReportType.equals(ARG_REPORT_TYPE_REGISTER))
				{
					RegisterReport ep = new RegisterReport(ledger);
					if ((target instanceof TenantEntry) ||
						(target instanceof PayeeEntry))
					{	// Target is a tenant/payee - create specific tenant/payee report
						sb.append(ep.buildRegisterReport(target, 0));
					}
					else
					{	// No target - create general register report			
						sb.append(ep.buildRegisterReport(0));
					}
				}
				else if (m_strReportType.equals(ARG_REPORT_TYPE_LEDGER))
				{
					LedgerReport lp = new LedgerReport(ledger);
					if ((target instanceof TenantEntry) ||
						(target instanceof PayeeEntry))
					{	// Target is a tenant/payee - create specific tenant/payee report
						sb.append(lp.buildLedgerReport(target, 0));
					}
					else
					{	// No target - create general register report			
						sb.append(lp.buildLedgerReport(0));
					}
				}
				else if (m_strReportType.equals(ARG_REPORT_TYPE_EXTRA))
				{
					sb.append("Extra Cents Paid Report:\n").append(ExtraPaidManager.getInstance().buildExtraPaidReport(1));
				}
				else if (m_strReportType.equals(ARG_REPORT_TYPE_COVERAGE))
				{
					CoverageReport cr = new CoverageReport(ledger);
					sb.append("Coverage Report:\n").append(cr.toString());
				}
				else if (m_strReportType.equals(ARG_REPORT_TYPE_BALANCES))
				{
					BalancesReport br = new BalancesReport(ledger);
					sb.append("Balances Report:\n").append(br.toString());
				}
				else if (m_strReportType.equals(ARG_REPORT_TYPE_PAYMENTS))
				{
					PaymentsReport pr = new PaymentsReport(ledger);
					sb.append("Payments Report:\n").append(pr.toString());
				}
				else if (m_strReportType.equals(ARG_REPORT_TYPE_CASHFLOW))
				{
					CashFlowReport cfr = new CashFlowReport(ledger);
					sb.append("Cash Flow Report:\n").append(cfr.toString());
				}
				if (null == m_fOutputFile)
				{
					System.out.println(sb.toString());
				}
				else
				{
					FileWriter fileWriter = new FileWriter(m_fOutputFile);
					String strWriteString = sb.toString();
					fileWriter.write(strWriteString, 0, strWriteString.length());
					fileWriter.close();
				}
			}
		}
		catch (Exception e)
		{
			System.out.println("ERROR:\n" + e.toString());
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	public static List<RegisterBaseEntry> getEntries()
	{
		return m_lEntries;
	}
	
	private static void processArguments(String[] args)
	{
		int iArgsIdx = 0;
		while (args.length > iArgsIdx)
		{
			String strTag = args[iArgsIdx++];
			if (strTag.equals(ARG_TAG_INFILE))
			{
				atEnd("Input file name must follow '-infile' parameter.", iArgsIdx, args);
				m_fInputFile = new File(args[iArgsIdx++]);
				if (!m_fInputFile.exists())
				{
					showUsage("Entries file: " + m_fInputFile.getAbsolutePath()+ " does not exist");
					System.exit(3);
				}
			}
			else if (strTag.equals(ARG_TAG_OUTFILE))
			{
				atEnd("Output file name must follow '-outfile' parameter.", iArgsIdx, args);
				m_fOutputFile = new File(args[iArgsIdx++]);
			}
			else if (strTag.equals(ARG_TAG_REPORT))
			{
				atEnd("Report type must follow 'report' parameter.", iArgsIdx, args);
				m_strReportType = args[iArgsIdx++];
				if (!REPORT_TYPES.contains(m_strReportType))
				{
					showUsage("Unrecognized report type: " + m_strReportType);
					System.exit(3);
				}
			}
			else if (strTag.equals(ARG_TAG_TARGET))
			{
				atEnd("Tenant or payee name must follow '-target' parameter.", iArgsIdx, args);
				m_strTarget = args[iArgsIdx];
				iArgsIdx++;
			}
			else
			{
				showUsage("Unknown argument tag: " + strTag);
				System.exit(3);
			}
		}
		
		if (null == m_fInputFile)
		{
			showUsage("Missing input file argument tag: " + ARG_TAG_INFILE);
			System.exit(3);
		}
		if (null == m_strReportType)
		{
			showUsage("Missing report type argument tag: " + ARG_TAG_REPORT);
			System.exit(3);
		}
	}
	
	private static void atEnd(String strError, int iArgsIdx, String[] args)
	{
		if (iArgsIdx >= args.length)
		{
			showUsage(strError);
			System.exit(3);
		}
	}
	
	private static RegisterBaseEntry resolveTarget(Register register, String strTarget)
	{
		if (StringUtil.isDefined(strTarget))
		{
			TenantEntry tenantEntry = ExtraPaidManager.getInstance().getTenant(strTarget);
			if (null != tenantEntry)
			{
				return tenantEntry;
			}
			PayeeEntry payeeEntry = Register.getPayeeEntry(register.getEntries(), strTarget);
			if (null != payeeEntry)
			{
				return payeeEntry;
			}
		}
		return null;
	}
	
	private static void showUsage(String strMessage)
	{
		if (StringUtil.isDefined(strMessage))
		{
			System.out.println(strMessage);
		}
		
		System.out.println("Usage:");
		System.out.println(ARG_TAG_INFILE);
		System.out.println("   The full path and filename of the input ledger file.");
		System.out.println(ARG_TAG_OUTFILE);
		System.out.println("   The full path and filename of the output report file. If omitted, output is sent to STDOUT.");
		System.out.println(ARG_TAG_REPORT);
		for (int i=0; i<REPORT_TYPES.size(); i++)
		{
			String strReportName = REPORT_TYPES.get(i);
			String strReportDescription = REPORT_DESCRIPTIONS.get(i);
			System.out.println("    " + strReportName);
			System.out.println("        " + strReportDescription);
		}
		System.out.println(ARG_TAG_TARGET);
		System.out.println("   The case-sensitive payee or tenant name. Accompanies register and ledger report types. Probably needs to be bound in dquotes.");
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("-inFile c:\\temp\\ledger.txt -outFile c:\\temp\\report.txt -report ledger -target \"Jane Austin\"");
		System.out.println("   (will output ledger report, filtered by tenant, to the indicated outFile)");
		System.out.println("-inFile c:\\temp\\ledger.txt -report ledger -target \"Dominion Energy\"");
		System.out.println("   (will output ledger report, filtered by payee, to stdout)");
		System.out.println("-inFile c:\\temp\\ledger.txt -report coverage");
		System.out.println("   (will output coverage report to stdout)");
	}
}
