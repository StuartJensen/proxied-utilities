package com.pp.proxied.utilities.reporting;

import internal.atlaslite.jcce.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.BalanceEntry;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.PayeeEntry;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.RemoveEntry;
import com.pp.proxied.utilities.schema.TenantEntry;

public class ApproximateReport
{
	private Ledger m_ledger;
	private TenantEntry m_tenantEntry;
	
	public ApproximateReport(Ledger ledger, TenantEntry tenantEntry)
	{
		m_ledger = ledger;
		m_tenantEntry = tenantEntry;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		List<LedgerEntry> ledgerEntries = m_ledger.getLedgerEntries();
		if ((null == ledgerEntries) || (ledgerEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		else
		{
			sb.append(StringUtil.getSpaces(iIndent)).append("Approximate Pending Owed Payments for tenant: ").append(m_tenantEntry.getTenantName()).append("\n");
			LedgerEntry moveOutLedgerEntry = Ledger.getMoveOutTenantLedgerEntry(ledgerEntries, m_tenantEntry.getTenantName());
			if (null == moveOutLedgerEntry)
			{
				sb.append(StringUtil.getSpaces(iIndent + 1)).append("Tenant: ").append(m_tenantEntry.getTenantName()).append(" does not have a MOVEOUT date!\n");
			}
			else
			{
				List<RemoveEntry> lRemovedPayees = new ArrayList<RemoveEntry>();
				sb.append(StringUtil.getSpaces(iIndent + 1)).append("Tenant MOVEOUT date: ").append(moveOutLedgerEntry.getDateString()).append("\n");
				Map<PayeeEntry, PaymentEntry> mapPayeesToLastPayment = new HashMap<PayeeEntry, PaymentEntry>();
				for (LedgerEntry ledgerEntry : ledgerEntries)
				{				
					List<PaymentEntry> lPayments = ledgerEntry.getActivePayments();
					if ((null != lPayments) && (!lPayments.isEmpty()))
					{
						for (PaymentEntry paymentEntry : lPayments)
						{
							mapPayeesToLastPayment.remove(paymentEntry.getAssociatedPayeeEntry());
							mapPayeesToLastPayment.put(paymentEntry.getAssociatedPayeeEntry(), paymentEntry);
						}
					}
					List<RemoveEntry> lRemoves = ledgerEntry.getActiveRemoves();
					if ((null != lRemoves) && (!lRemoves.isEmpty()))
					{
						for (RemoveEntry removeEntry : lRemoves)
						{
							Entry entry = removeEntry.getAssociatedTargetEntry();
							if (entry instanceof PayeeEntry)
							{
								lRemovedPayees.add(removeEntry);
							}
						}
					}
					List<PayeeEntry> lPayees = ledgerEntry.getActivePayees();
					if ((null != lPayees) && (!lPayees.isEmpty()))
					{
						for (PayeeEntry payeeEntry : lPayees)
						{
							RemoveEntry removeEntry = Ledger.getRemoveEntry(lRemovedPayees, payeeEntry.getPayeeName());
							if (null != removeEntry)
							{	// Previously removed payee must have been re-activated
								lRemovedPayees.remove(removeEntry);
							}
						}
					}
				}
				
				Calendar tenantMoveOutDate = moveOutLedgerEntry.getDate();
				MoneyInteger totalOwed = MoneyInteger.ZERO;
				Iterator<PayeeEntry> iterPayees = mapPayeesToLastPayment.keySet().iterator();
				while (iterPayees.hasNext())
				{
					PayeeEntry payeeEntry = iterPayees.next();
					PaymentEntry lastPaymentEntry = mapPayeesToLastPayment.get(payeeEntry);
					sb.append(StringUtil.getSpaces(iIndent+1)).append(payeeEntry.getPayeeName()).append(": ");
					// Is this a removed payee?
					RemoveEntry removeEntry = Ledger.getRemoveEntry(lRemovedPayees, payeeEntry.getPayeeName());
					if (null != removeEntry)
					{
						sb.append("Payee was removed on ").append(removeEntry.getDateString()).append("\n");
					}
					else
					{
						if (moveOutLedgerEntry.isBefore(lastPaymentEntry.getEndDate()) ||
							moveOutLedgerEntry.isOn(lastPaymentEntry.getEndDate()))
						{
							sb.append("Tenant is paid in full. Last payment paid thru: ").append(lastPaymentEntry.getDateString()).append("\n");
						}
						else
						{	// Tenant moved out after last payment end service date
							ActiveTenantsVisitor activeTenantsVisitor = (ActiveTenantsVisitor)moveOutLedgerEntry.getVisitor(ActiveTenantsVisitor.class);
							int iActiveTenants = activeTenantsVisitor.getActiveTenantCount();
							// Get amount per day of last payment
							MoneyInteger amountPerDay = lastPaymentEntry.getAmountPerDay();
							// Get number of days between last payment end service date and tenant move out date
							int iDaysInPeriod = Entry.getDaysInPeriod(lastPaymentEntry.getEndDate(), tenantMoveOutDate);
							MoneyInteger owed = new MoneyInteger(iDaysInPeriod).multiply(amountPerDay.divide(new MoneyInteger(iActiveTenants)));
							sb.append("Tenant owes approximately $").append(owed.toString());
							sb.append(" for ").append(iDaysInPeriod).append(" days between ").append(Entry.getDateString(lastPaymentEntry.getEndDate()));
							sb.append(" and ").append(moveOutLedgerEntry.getDateString()).append("\n");
							totalOwed = totalOwed.plus(owed);
						}
					}
				}
				
				// Get tenant's account balance on the day of the last ledger entry
				LedgerEntry lastLedgerEntry = ledgerEntries.get(ledgerEntries.size() - 1);
				BalancesVisitor balancesVisitor = (BalancesVisitor)lastLedgerEntry.getVisitor(BalancesVisitor.class);
				if (null != balancesVisitor)
				{
					MoneyInteger tenantBalance = balancesVisitor.getBalance(m_tenantEntry);
					if (null != tenantBalance)
					{
						sb.append(StringUtil.getSpaces(iIndent+1));
						sb.append("As of last ledger entry date ").append(Entry.getDateString(lastLedgerEntry.getDate()));
						sb.append(" tenant's account balance is $").append(tenantBalance.toString()).append("\n");
						totalOwed = totalOwed.minus(tenantBalance);
					}
				}
				sb.append(StringUtil.getSpaces(iIndent+1));
				sb.append("Approximate total owed: $").append(totalOwed.toString()).append("\n");
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
}
