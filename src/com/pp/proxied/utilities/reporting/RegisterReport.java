package com.pp.proxied.utilities.reporting;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class RegisterReport
{
	private Ledger m_ledger;
	
	public RegisterReport(Ledger ledger)
	{
		m_ledger = ledger;
	}
	
	public String buildRegisterReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		for (LedgerEntry entry : m_ledger.getLedgerEntries())
		{
			sb.append(buildRegisterReport(entry, iIndent));
		}
		return sb.toString();
	}
	
	public String buildRegisterReport(RegisterBaseEntry target, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getSpaces(iIndent));
		if (target instanceof TenantEntry)
		{
			sb.append("On behalf of tenant ").append( ((TenantEntry)target).getTenantName()).append("\n");
		}
		else if (target instanceof PayeeEntry)
		{
			sb.append("On behalf of payee ").append( ((PayeeEntry)target).getPayeeName()).append("\n");
		}
		for (LedgerEntry entry : m_ledger.getLedgerEntries())
		{
			sb.append(StringUtil.getSpaces(iIndent));
			if (target instanceof TenantEntry)
			{
				sb.append(buildRegisterReport(entry, (TenantEntry)target, iIndent));
			}
			else if (target instanceof PayeeEntry)
			{
				sb.append(buildRegisterReport(entry, (PayeeEntry)target, iIndent));
			}
			else
			{
				throw new IllegalStateException("A register report may be filtered using a payee of a tenant, not a " + target.getClass().getSimpleName());
			}
		}
		return sb.toString();
	}
	
	private String buildRegisterReport(LedgerEntry entry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != entry.getRegisterTenants()) && (!entry.getRegisterTenants().isEmpty()))
		{
			for (TenantEntry tenant : entry.getRegisterTenants().getTenants())
			{
				sb.append(tenant.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterPayees()) && (!entry.getRegisterPayees().isEmpty()))
		{
			for (PayeeEntry payee : entry.getRegisterPayees().getPayees())
			{
				sb.append(payee.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterDeposits()) && (!entry.getRegisterDeposits().isEmpty()))
		{
			for (DepositEntry deposit : entry.getRegisterDeposits().getDeposits())
			{
				sb.append(deposit.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterBalances()) && (!entry.getRegisterBalances().isEmpty()))
		{
			for (BalanceEntry balance : entry.getRegisterBalances().getBalances())
			{
				sb.append(balance.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterRemoves()) && (!entry.getRegisterRemoves().isEmpty()))
		{
			for (RemoveEntry remove : entry.getRegisterRemoves().getRemoves())
			{
				sb.append(remove.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterPayments()) && (!entry.getRegisterPayments().isEmpty()))
		{
			for (PaymentEntry payment : entry.getRegisterPayments().getPayments())
			{
				sb.append(payment.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterMoveOuts()) && (!entry.getRegisterMoveOuts().isEmpty()))
		{
			for (MoveOutEntry moveOut : entry.getRegisterMoveOuts().getMoveOuts())
			{
				sb.append(moveOut.buildRegisterReport(iIndent + 1));
			}
		}
		if ((null != entry.getRegisterFlushes()) && (!entry.getRegisterFlushes().isEmpty()))
		{
			for (FlushEntry flush : entry.getRegisterFlushes().getFlushes())
			{
				sb.append(flush.buildRegisterReport(iIndent + 1));
			}
		}
		if (0 == sb.length())
		{
			return "";
		}
		return new StringBuilder(RegisterReport.buildHeader(entry, iIndent)).append(sb.toString()).toString();
	}
	
	private String buildRegisterReport(LedgerEntry entry, TenantEntry tenantEntry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != entry.getRegisterTenants()) && (!entry.getRegisterTenants().isEmpty()))
		{
			for (TenantEntry tenant : entry.getRegisterTenants().getTenants())
			{
				if (tenantEntry.getTenantName().equals(tenant.getTenantName()))
				{
					sb.append(tenantEntry.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterMoveOuts()) && (!entry.getRegisterMoveOuts().isEmpty()))
		{
			for (MoveOutEntry moveOut : entry.getRegisterMoveOuts().getMoveOuts())
			{
				if (tenantEntry.getTenantName().equals(moveOut.getTenantName()))
				{
					sb.append(moveOut.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterRemoves()) && (!entry.getRegisterRemoves().isEmpty()))
		{
			for (RemoveEntry remove : entry.getRegisterRemoves().getRemoves())
			{
				if (tenantEntry.getTenantName().equals(remove.getTargetName()))
				{
					sb.append(remove.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterBalances()) && (!entry.getRegisterBalances().isEmpty()))
		{
			for (BalanceEntry balance : entry.getRegisterBalances().getBalances())
			{
				if (tenantEntry.getTenantName().equals(balance.getTenantName()))
				{
					sb.append(balance.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterFlushes()) && (!entry.getRegisterFlushes().isEmpty()))
		{
			for (FlushEntry flush : entry.getRegisterFlushes().getFlushes())
			{
				if ((tenantEntry.getTenantName().equals(flush.getFromTenantName())) ||
					(tenantEntry.getTenantName().equals(flush.getToTenantName())))
				{
					sb.append(flush.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterDeposits()) && (!entry.getRegisterDeposits().isEmpty()))
		{
			for (DepositEntry deposit : entry.getRegisterDeposits().getDeposits())
			{
				if (tenantEntry.getTenantName().equals(deposit.getTenantName()))
				{
					sb.append(deposit.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if (0 == sb.length())
		{
			return "";
		}
		return new StringBuilder(RegisterReport.buildHeader(entry, iIndent)).append(sb.toString()).toString();
	}
	
	private String buildRegisterReport(LedgerEntry entry, PayeeEntry payeeEntry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != entry.getRegisterPayees()) && (!entry.getRegisterPayees().isEmpty()))
		{
			for (PayeeEntry payee : entry.getRegisterPayees().getPayees())
			{
				if (payeeEntry.getPayeeName().equals(payee.getPayeeName()))
				{
					sb.append(payee.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterRemoves()) && (!entry.getRegisterRemoves().isEmpty()))
		{
			for (RemoveEntry remove : entry.getRegisterRemoves().getRemoves())
			{
				if (payeeEntry.getPayeeName().equals(remove.getTargetName()))
				{
					sb.append(remove.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterPayments()) && (!entry.getRegisterPayments().isEmpty()))
		{
			for (PaymentEntry payment : entry.getRegisterPayments().getPayments())
			{
				if (payeeEntry.getPayeeName().equals(payment.getAssociatedPayeeEntry().getPayeeName()))
				{
					sb.append(payment.buildRegisterReport(iIndent + 1));
				}
			}
		}
		if (0 == sb.length())
		{
			return "";
		}
		return new StringBuilder(RegisterReport.buildHeader(entry, iIndent)).append(sb.toString()).toString();
	}
	
	public static String buildHeader(LedgerEntry entry, int iIndent)
	{
		StringBuilder sb = new StringBuilder(StringUtil.getSpaces(iIndent));
		sb.append("Date: ");
		sb.append(DateUtil.getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, entry.getDate()));
		sb.append("\n");
		return sb.toString();
	}
}
