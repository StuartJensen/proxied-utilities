package com.pp.proxied.utilities.reporting;

import java.text.MessageFormat;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.schema.ActiveTenant;
import com.pp.proxied.utilities.ledger.schema.PassiveTenant;
import com.pp.proxied.utilities.ledger.schema.RegisterRemoves;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class BalancesReport
{
	private Ledger m_ledger;
	
	public BalancesReport(Ledger ledger)
	{
		m_ledger = ledger;
	}
	
	private static final String FLUSH_HEADER = "{0}: FLUSH Tenant {1} balance of {2} to LANDLORD";
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		List<LedgerEntry>lEntries = m_ledger.getLedgerEntries();
		if ((null == lEntries) || (lEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		
		for (LedgerEntry entry : lEntries)
		{
			List<ActiveTenant> lActiveTenants = null;
			List<PassiveTenant> lPassiveTenants = null;
			if ((null != entry.getActiveTenants()) && (!entry.getActiveTenants().isEmpty()))
			{
				lActiveTenants = entry.getActiveTenants().getActiveTenants();
			}
			if ((null != entry.getPassiveTenants()) && (!entry.getPassiveTenants().isEmpty()))
			{
				lPassiveTenants = entry.getPassiveTenants().getPassiveTenants();
			}
			
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbLine = new StringBuilder();
			sbLine.append(entry.getDateString()).append(": ");
			if (null != lActiveTenants)
			{
				for (ActiveTenant current : lActiveTenants)
				{
					sbLine.append("[").append(current.getTenant().getTenantName()).append(",").append(current.getBalance().toString()).append("]");
				}
			}
			if (null != lPassiveTenants)
			{
				for (PassiveTenant current : lPassiveTenants)
				{
					sbLine.append("[*").append(current.getTenant().getTenantName()).append(",").append(current.getBalance().toString()).append("]");
				}
			}
			
			// Process REMOVE entries so that we can flush balance to LANDLORD
			RegisterRemoves registerRemoves = entry.getRegisterRemoves();
			if ((null != registerRemoves) && (!registerRemoves.isEmpty()))
			{
				List<RemoveEntry> lRemoveEntries = registerRemoves.getRemoves();
				for (RemoveEntry remove : lRemoveEntries)
				{	// Is this a remove of a TENANT?
					if (remove.getAssociatedTargetEntry() instanceof TenantEntry)
					{	// Yes
						LedgerEntry previousEntry = entry.getPreviousEntry();
						if (null != previousEntry)
						{
							TenantEntry tenantEntry = (TenantEntry)remove.getAssociatedTargetEntry();
							ActiveTenant removedActiveTenant = previousEntry.getActiveTenant(tenantEntry);
							if (null != removedActiveTenant)
							{
								TenantEntry.addToLandlordBalance(removedActiveTenant.getBalance());
								sbHeader.append(MessageFormat.format(FLUSH_HEADER, entry.getDateString(), tenantEntry.getTenantName(), removedActiveTenant.getBalance().toString()));
							}
							else
							{
								PassiveTenant removedPassiveTenant = previousEntry.getPassiveTenant(tenantEntry);
								if (null != removedPassiveTenant)
								{
									TenantEntry.addToLandlordBalance(removedPassiveTenant.getBalance());
									sbHeader.append(MessageFormat.format(FLUSH_HEADER, entry.getDateString(), tenantEntry.getTenantName(),removedPassiveTenant.getBalance().toString()));
								}
							}
						}
					}
				}
			}
			sbLine.append("[LANDLORD,").append(TenantEntry.getLandlordBalance().toString()).append("]");
			
			if (0 < sbHeader.length())
			{
				sb.append(sbHeader).append("\n");
			}
			sb.append(sbLine).append("\n");
		}
		
/*		
		else
		{
			sb.append(StringUtil.getSpaces(iIndent)).append("Cash Flow Report:\n");
			for (RegisterEntry entry : registerEntries)
			{
				StringBuilder sbTransactions = new StringBuilder();
				List<DepositEntry> lDeposits = entry.getRegistryDeposits();
				if ((null != lDeposits) && (!lDeposits.isEmpty()))
				{
					for (DepositEntry depositEntry : lDeposits)
					{
						sbTransactions.append(StringUtil.getSpaces(iIndent+2)).append("Deposit: From: ").append(depositEntry.getTenantName()).append(", $").append(depositEntry.getAmount().toString()).append("\n");
					}
				}
				List<PaymentEntry> lPayments = entry.getRegistryPayments();
				if ((null != lPayments) && (!lPayments.isEmpty()))
				{
					for (PaymentEntry paymentEntry : lPayments)
					{
						sbTransactions.append(StringUtil.getSpaces(iIndent+2)).append("Payment: To: ").append(paymentEntry.getPayeeName()).append(", $").append(paymentEntry.getAmount().toString()).append("\n");
					}
				}
				List<BalanceEntry> lBalances = entry.getRegistryBalances();
				if ((null != lBalances) && (!lBalances.isEmpty()))
				{
					for (BalanceEntry balanceEntry : lBalances)
					{
						sbTransactions.append(StringUtil.getSpaces(iIndent+2)).append("Balance Set: For: ").append(balanceEntry.getTenantName()).append(", $").append(balanceEntry.getBalance().toString().toString()).append("\n");
					}
				}
				if (0 != sbTransactions.length())
				{
					sb.append(StringUtil.getSpaces(iIndent+1)).append(entry.getDateString()).append(": ");
					BalancesVisitor balancesVisitor = (BalancesVisitor)entry.getVisitor(BalancesVisitor.class);
					if (null != balancesVisitor)
					{
						MoneyInteger total = balancesVisitor.getTotalBalance();
						sb.append("Running Balance: ").append(", $").append(total.toString().toString()).append("\n");
					}
					sb.append(sbTransactions);
				}
			}
		}
*/		
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}

}
