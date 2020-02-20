package com.pp.proxied.utilities;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.reporting.ActiveTenantsVisitor;
import com.pp.proxied.utilities.reporting.ApproximateReport;
import com.pp.proxied.utilities.reporting.BalancesVisitor;
import com.pp.proxied.utilities.reporting.CashFlowReport;
import com.pp.proxied.utilities.reporting.CoverageReport;
import com.pp.proxied.utilities.reporting.ExtraPaidReport;
import com.pp.proxied.utilities.reporting.PaymentPercentageVisitor;
import com.pp.proxied.utilities.reporting.PaymentsVisitor;
import com.pp.proxied.utilities.reporting.SpecificPayeeReport;
import com.pp.proxied.utilities.reporting.SpecificTenantReport;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.PayeeEntry;
import com.pp.proxied.utilities.schema.TenantEntry;

import internal.atlaslite.jcce.util.DateTimeUtil;
import internal.atlaslite.jcce.util.StringUtil;

public class ProxiedUtilities
{
	private static final int ARGS_MIN_COUNT = 4;
	private static final int ARGS_MAX_COUNT = 10;
	
	private static final String ARG_TAG_INFILE = "-inFile";
	private static final String ARG_TAG_OUTFILE = "-outFile";
	private static final String ARG_TAG_REPORT = "-report";
	private static final String ARG_TAG_PAYEE = "-payee";
	private static final String ARG_TAG_TENANT = "-tenant";
	
	private static final String ARG_REPORT_TYPE_LEDGER = "ledger";
	private static final String ARG_REPORT_TYPE_TENANT = "tenant";
	private static final String ARG_REPORT_TYPE_PAYEE = "payee";
	private static final String ARG_REPORT_TYPE_BALANCES = "balances";
	private static final String ARG_REPORT_TYPE_COVERAGE = "coverage";
	private static final String ARG_REPORT_TYPE_EXTRA = "extra";
	private static final String ARG_REPORT_TYPE_CASHFLOW = "cashflow";
	private static final String ARG_REPORT_TYPE_APPROX = "approx";
	
	private static List<String> REPORT_TYPES;
	private static List<String> REPORT_DESCRIPTIONS;
	
	static
	{
		REPORT_TYPES = new ArrayList<String>();
		REPORT_TYPES.add(ARG_REPORT_TYPE_LEDGER);
		REPORT_TYPES.add(ARG_REPORT_TYPE_TENANT);
		REPORT_TYPES.add(ARG_REPORT_TYPE_PAYEE);
		REPORT_TYPES.add(ARG_REPORT_TYPE_BALANCES);
		REPORT_TYPES.add(ARG_REPORT_TYPE_COVERAGE);
		REPORT_TYPES.add(ARG_REPORT_TYPE_EXTRA);
		REPORT_TYPES.add(ARG_REPORT_TYPE_CASHFLOW);
		REPORT_TYPES.add(ARG_REPORT_TYPE_APPROX);
		REPORT_DESCRIPTIONS = new ArrayList<String>();
		REPORT_DESCRIPTIONS.add("A simple ledger regurgitation");
		REPORT_DESCRIPTIONS.add("Details a specified tenant's activities. Requires " + ARG_TAG_TENANT + ".");
		REPORT_DESCRIPTIONS.add("Details a specified payee's activities. Requires " + ARG_TAG_PAYEE + ".");
		REPORT_DESCRIPTIONS.add("Details total and per tenant account balances each tine a transaction happens.");
		REPORT_DESCRIPTIONS.add("Details payee service time coverage (1 is desirable) and total active tenants coverage (3 is desirable).");
		REPORT_DESCRIPTIONS.add("Payments with odd amounts require one tenant to pay one cent extra. This extra payment should be rotated fairly among tenants. Details how much each tenant has paid in extra cents.");
		REPORT_DESCRIPTIONS.add("Typical cash flow ledger report.");
		REPORT_DESCRIPTIONS.add("Approximates the payments still owed by a MOVEOUT tenant to active payees. Requires " + ARG_TAG_TENANT + ".");
	};
	
	private static String m_strReportType = null;
	private static String m_strTarget = null;
	private static File m_fOutputFile = null;
	private static File m_fInputFile = null;
	
	public static SimpleDateFormat DAYHOUR_DATEFORMAT = new SimpleDateFormat("MM/dd/yyyy 'at' HH:mm:ss");
	
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
			EntryParser parser = new EntryParser(m_fInputFile);
			List<Entry> lEntries = parser.parse();
			
			if ((null != lEntries) && (!lEntries.isEmpty()))
			{
				Ledger.validate(lEntries);
				
				Ledger ledger = new Ledger(lEntries);
				Calendar today = Calendar.getInstance();
				String strReportGenerationTime = DateTimeUtil.getTime(DAYHOUR_DATEFORMAT, today.getTime().getTime());
				sb.append("Report Generated on ").append(strReportGenerationTime).append("\n");
				if (m_strReportType.equals(ARG_REPORT_TYPE_LEDGER))
				{
					sb.append(ledger.toString(0));
				}
				else
				{
					ActiveTenantsVisitor activeTenantsRoot = new ActiveTenantsVisitor(); 
					ledger.attach(activeTenantsRoot, true);
					
					PaymentPercentageVisitor paymentsPercentageRoot = new PaymentPercentageVisitor(); 
					ledger.attach(paymentsPercentageRoot, true);
					
					PaymentsVisitor paymentsRoot = new PaymentsVisitor(); 
					ledger.attach(paymentsRoot, true);
					
					BalancesVisitor balancesRoot = new BalancesVisitor(); 
					ledger.attach(balancesRoot, true);
					if (m_strReportType.equals(ARG_REPORT_TYPE_BALANCES))
					{
						sb.append("Balances:\n").append(ledger.toString(balancesRoot, 3));
					}
					else if (m_strReportType.equals(ARG_REPORT_TYPE_TENANT))
					{
						TenantEntry tenantEntry = Ledger.getTenantEntry(lEntries, m_strTarget);
						SpecificTenantReport reportTenant = new SpecificTenantReport(ledger, tenantEntry);
						sb.append("Tenant Report:\n").append(reportTenant.toString());
					}
					else if (m_strReportType.equals(ARG_REPORT_TYPE_PAYEE))
					{
						PayeeEntry payeeEntry = Ledger.getPayeeEntry(lEntries, m_strTarget);
						SpecificPayeeReport reportPayee = new SpecificPayeeReport(ledger, payeeEntry);
						sb.append("Payee Report:\n").append(reportPayee.toString(0));
					}
					else if (m_strReportType.equals(ARG_REPORT_TYPE_COVERAGE))
					{
						CoverageReport cr = new CoverageReport(ledger);
						sb.append("Coverage Report:\n").append(cr.toString());
					}
					else if (m_strReportType.equals(ARG_REPORT_TYPE_EXTRA))
					{
						ExtraPaidReport report = new ExtraPaidReport(ledger);
						sb.append(report.toString());
					}
					else if (m_strReportType.equals(ARG_REPORT_TYPE_CASHFLOW))
					{
						CashFlowReport report = new CashFlowReport(ledger);
						sb.append(report.toString());
					}
					else if (m_strReportType.equals(ARG_REPORT_TYPE_APPROX))
					{
						TenantEntry tenantEntry = Ledger.getTenantEntry(lEntries, m_strTarget);
						ApproximateReport report = new ApproximateReport(ledger, tenantEntry);
						sb.append(report.toString());
					}
				}
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
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	private static void processArguments(String[] args)
	{
		int iArgsIdx = 0;
		while (args.length > iArgsIdx)
		{
			String strTag = args[iArgsIdx];
			if (strTag.equals(ARG_TAG_INFILE))
			{
				m_fInputFile = new File(args[++iArgsIdx]);
				if (!m_fInputFile.exists())
				{
					showUsage("Entries file: " + args[iArgsIdx] + " does not exist");
					System.exit(3);
				}
				iArgsIdx++;
			}
			else if (strTag.equals(ARG_TAG_OUTFILE))
			{
				m_fOutputFile = new File(args[++iArgsIdx]);
				iArgsIdx++;
			}
			else if (strTag.equals(ARG_TAG_REPORT))
			{
				m_strReportType = args[++iArgsIdx];
				if (!StringUtil.isStringIn(m_strReportType, REPORT_TYPES))
				{
					showUsage("Unrecognized report type: " + m_strReportType);
					System.exit(3);
				}
				iArgsIdx++;
			}
			else if (strTag.equals(ARG_TAG_PAYEE))
			{
				m_strTarget = args[++iArgsIdx];
				iArgsIdx++;
			}
			else if (strTag.equals(ARG_TAG_TENANT))
			{
				m_strTarget = args[++iArgsIdx];
				iArgsIdx++;
			}
			else
			{
				showUsage("Unknown argument tag: " + args[iArgsIdx]);
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

		if ((m_strReportType.equals(ARG_REPORT_TYPE_TENANT)) ||
		    (m_strReportType.equals(ARG_REPORT_TYPE_PAYEE)))
		{
			if (args.length != ARGS_MAX_COUNT)
			{
				if (null == m_strTarget)
				{
					showUsage("Report type \"" + m_strReportType + "\" requires a tenant or a payee name");
					System.exit(6);
				}
			}
		}
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
		System.out.println(ARG_TAG_PAYEE);
		System.out.println("   The case-sensitive payee name. Accompanies report types that require a payee. Probably needs to be bound in dquotes.");
		System.out.println(ARG_TAG_TENANT);
		System.out.println("   The case-sensitive tenant name. Accompanies report types that require a tenant. Probably needs to be bound in dquotes.");
		System.out.println("");
		System.out.println("Examples:");
		System.out.println("-inFile c:\\temp\\ledger.txt -outFile c:\\temp\\report.txt -report tenant -tenant \"Bethany Hardy\"");
		System.out.println("   (will output tenant report to the indicated outFile)");
		System.out.println("-inFile c:\\temp\\ledger.txt -report payee -payee \"Dominion Energy\"");
		System.out.println("   (will output payee report to stdout)");
		System.out.println("-inFile c:\\temp\\ledger.txt -report coverage");
		System.out.println("   (will output coverage report to stdout)");
	}
}
