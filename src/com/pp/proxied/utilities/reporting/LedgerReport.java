package com.pp.proxied.utilities.reporting;

import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenant;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayment;
import com.pp.proxied.utilities.ledger.schema.PassiveTenant;
import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.GenericDouble;
import com.pp.proxied.utilities.util.StringUtil;

public class LedgerReport
{
	private Ledger m_ledger;
	
	public LedgerReport(Ledger ledger)
	{
		m_ledger = ledger;
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		for (LedgerEntry entry : m_ledger.getLedgerEntries())
		{
			sb.append(buildLedgerReport(entry, iIndent));
		}
		return sb.toString();
	}
	
	public String buildLedgerReport(RegisterBaseEntry target, int iIndent)
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
		else
		{
			throw new IllegalStateException("A ledger report may be filtered using a payee of a tenant, not a " + target.getClass().getSimpleName());
		}
		for (LedgerEntry entry : m_ledger.getLedgerEntries())
		{
			sb.append(StringUtil.getSpaces(iIndent));
			if (target instanceof TenantEntry)
			{
				sb.append(buildLedgerReport(entry, (TenantEntry)target, iIndent));
			}
			else if (target instanceof PayeeEntry)
			{
				sb.append(buildLedgerReport(entry, (PayeeEntry)target, iIndent));
			}
		}
		return sb.toString();
	}
	
	public String buildLedgerReport(LedgerEntry entry, int iIndent)
	{
		StringBuilder sb = new StringBuilder(StringUtil.getSpaces(iIndent));
		if (null != entry.getRegisterTenants() && (!entry.getRegisterTenants().isEmpty()))
		{
			sb.append(entry.getRegisterTenants().buildLedgerReport(iIndent + 1));
		}
		if (null != entry.getRegisterDeposits() && (!entry.getRegisterDeposits().isEmpty()))
		{
			sb.append(entry.getRegisterDeposits().buildLedgerReport(iIndent + 1));
		}
		if (null != entry.getRegisterPayees() && (!entry.getRegisterPayees().isEmpty()))
		{
			sb.append(entry.getRegisterPayees().buildLedgerReport(iIndent + 1));
		}
		if (null != entry.getRegisterPayments() && (!entry.getRegisterPayments().isEmpty()))
		{
			sb.append(entry.getRegisterPayments().buildLedgerReport(iIndent + 1));
		}
		if (null != entry.getRegisterMoveOuts() && (!entry.getRegisterMoveOuts().isEmpty()))
		{
			sb.append(entry.getRegisterMoveOuts().buildLedgerReport(iIndent + 1));
		}
		if (null != entry.getRegisterRemoves() && (!entry.getRegisterRemoves().isEmpty()))
		{
			sb.append(entry.getRegisterRemoves().buildLedgerReport(iIndent + 1));
		}
		if (null != entry.getActiveTenants() && (!entry.getActiveTenants().isEmpty()))
		{
			sb.append(entry.getActiveTenants().toString(iIndent + 1));
		}
		if (null != entry.getPassiveTenants() && (!entry.getPassiveTenants().isEmpty()))
		{
			sb.append(entry.getPassiveTenants().toString(iIndent + 1));
		}
		if (null != entry.getActivePayments() && (!entry.getActivePayments().isEmpty()))
		{
			sb.append(entry.getActivePayments().toString(iIndent + 1));
		}
		if (0 == sb.length())
		{
			return "";
		}
		return new StringBuilder(RegisterReport.buildHeader(entry, iIndent)).append(sb.toString()).toString();
	}

	public String buildLedgerReport(LedgerEntry entry, PayeeEntry payeeEntry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != entry.getRegisterPayees()) && (!entry.getRegisterPayees().isEmpty()))
		{
			for (PayeeEntry payee : entry.getRegisterPayees().getPayees())
			{
				if (payeeEntry.getPayeeName().equals(payee.getPayeeName()))
				{
					sb.append(payee.buildLedgerReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterRemoves()) && (!entry.getRegisterRemoves().isEmpty()))
		{
			for (RemoveEntry remove : entry.getRegisterRemoves().getRemoves())
			{
				if (payeeEntry.getPayeeName().equals(remove.getTargetName()))
				{
					sb.append(remove.buildLedgerReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterPayments()) && (!entry.getRegisterPayments().isEmpty()))
		{
			for (PaymentEntry payment : entry.getRegisterPayments().getPayments())
			{
				if (payeeEntry.getPayeeName().equals(payment.getAssociatedPayeeEntry().getPayeeName()))
				{
					sb.append(payment.buildLedgerReport(iIndent + 1));
				}
			}
		}
		
		List<ActiveTenantPayment> tenantActivePayments = entry.getActiveTenantPayments(payeeEntry);
		for (ActiveTenantPayment payment : tenantActivePayments)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Received: $").append(payment.getPaidAmount()).append(" from tenant: ").append(payment.getActiveTenant().getTenant().getTenantName()).append("\n");
		}
		
		if (0 == sb.length())
		{
			return "";
		}
		return new StringBuilder(RegisterReport.buildHeader(entry, iIndent)).append(sb.toString()).toString();
	}
	
	public String buildLedgerReport(LedgerEntry entry, TenantEntry tenantEntry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != entry.getRegisterTenants()) && (!entry.getRegisterTenants().isEmpty()))
		{
			for (TenantEntry tenant : entry.getRegisterTenants().getTenants())
			{
				if (tenantEntry.getTenantName().equals(tenant.getTenantName()))
				{
					sb.append(tenant.buildLedgerReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterMoveOuts()) && (!entry.getRegisterMoveOuts().isEmpty()))
		{
			for (MoveOutEntry moveOut : entry.getRegisterMoveOuts().getMoveOuts())
			{
				if (tenantEntry.getTenantName().equals(moveOut.getTenantName()))
				{
					sb.append(moveOut.buildLedgerReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterRemoves()) && (!entry.getRegisterRemoves().isEmpty()))
		{
			for (RemoveEntry remove : entry.getRegisterRemoves().getRemoves())
			{
				if (tenantEntry.getTenantName().equals(remove.getTargetName()))
				{
					sb.append(remove.buildLedgerReport(iIndent + 1));
				}
			}
		}
		if ((null != entry.getRegisterBalances()) && (!entry.getRegisterBalances().isEmpty()))
		{
			for (BalanceEntry balance : entry.getRegisterBalances().getBalances())
			{
				if (tenantEntry.getTenantName().equals(balance.getTenantName()))
				{
					sb.append(balance.buildLedgerReport(iIndent + 1));
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
					sb.append(flush.buildLedgerReport(iIndent + 1));
				}
			}
		}

		List<GenericDouble<ActivePayment, ActiveTenantPayment>> tenantActivePayments = entry.getActiveTenantPayments(tenantEntry);
		for (GenericDouble<ActivePayment, ActiveTenantPayment> payment : tenantActivePayments)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Paid: ").append(payment.first.getPayment().getPayeeName()).append(": $").append(payment.second.getPaidAmount()).append("\n");
		}
		
		if ((null != entry.getRegisterDeposits()) && (!entry.getRegisterDeposits().isEmpty()))
		{
			for (DepositEntry deposit : entry.getRegisterDeposits().getDeposits())
			{
				if (tenantEntry.getTenantName().equals(deposit.getTenantName()))
				{
					sb.append(deposit.buildLedgerReport(iIndent + 1));
				}
			}
		}

		ActiveTenant activeTenant = entry.getActiveTenant(tenantEntry);
		if (null != activeTenant)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Balance: ").append(activeTenant.getBalance().toString()).append("\n");
		}
		else
		{
			PassiveTenant passiveTenant = entry.getPassiveTenant(tenantEntry);
			if (null != passiveTenant)
			{
				sb.append(StringUtil.getSpaces(iIndent + 1)).append("Balance: ").append(passiveTenant.getBalance().toString()).append("\n");
			}
		}
		
		if (0 == sb.length())
		{
			return "";
		}
		return new StringBuilder(RegisterReport.buildHeader(entry, iIndent)).append(sb.toString()).toString();
	}

}
