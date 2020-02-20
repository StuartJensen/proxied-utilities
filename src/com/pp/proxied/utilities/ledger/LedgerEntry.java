package com.pp.proxied.utilities.ledger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.reporting.BalancesVisitor;
import com.pp.proxied.utilities.reporting.PaymentsVisitor;
import com.pp.proxied.utilities.schema.BalanceEntry;
import com.pp.proxied.utilities.schema.DepositEntry;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.FlushEntry;
import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.MoveOutEntry;
import com.pp.proxied.utilities.schema.PayeeEntry;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.RemoveEntry;
import com.pp.proxied.utilities.schema.TenantEntry;
import com.pp.proxied.utilities.util.CollectionUtil;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.ObjectUtil;
import com.pp.proxied.utilities.util.PaymentDetails;
import com.pp.proxied.utilities.util.StringUtil;

public class LedgerEntry
{
	private int m_iNthEntryInLedger;
	private List<TenantEntry> m_lActiveTenants;
	private List<PayeeEntry> m_lActivePayees;
	private List<BalanceEntry> m_lActiveBalances;
	private List<PaymentEntry> m_lActivePayments;
	private List<DepositEntry> m_lActiveDeposits;
	private List<MoveOutEntry> m_lActiveMoveOuts;
	private List<RemoveEntry> m_lActiveRemoves;
	private List<FlushEntry> m_lActiveFlushes;
	private Calendar m_date;
	
	private List<LedgerEntryVisitor> m_lVisitors;
	
	public LedgerEntry(Calendar date, int iNthEntryInLedger)
	{
		m_date = date;
		m_iNthEntryInLedger = iNthEntryInLedger;
	}
	
	public Calendar getDate()
	{
		Calendar result = Calendar.getInstance();
		result.setTimeInMillis(m_date.getTimeInMillis());
		return result;
	}
	
	public String getDateString()
	{
		return DateUtil.getTime(Entry.STANDARD_DATEFORMAT, m_date.getTimeInMillis());
	}
	
	public int getNthEntryInledger()
	{
		return m_iNthEntryInLedger;
	}
	
	public boolean isBefore(Calendar that)
	{
		return (0 > Entry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOnOrBefore(Calendar that)
	{
		return (0 >= Entry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isAfter(Calendar that)
	{
		return (0 < Entry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOnOrAfter(Calendar that)
	{
		return (0 <= Entry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOn(Calendar that)
	{
		return (0 == Entry.compareMonthDayYear(getDate(), that));
	}

	public void addTenant(TenantEntry tenant)
	{
		if (null == m_lActiveTenants)
		{
			m_lActiveTenants = new ArrayList<TenantEntry>();
		}
		m_lActiveTenants.add(tenant);
	}
	
	public List<TenantEntry> getActiveTenants()
	{
		return m_lActiveTenants;
	}

	public void addMoveOut(MoveOutEntry moveOut)
	{
		if (null == m_lActiveMoveOuts)
		{
			m_lActiveMoveOuts = new ArrayList<MoveOutEntry>();
		}
		m_lActiveMoveOuts.add(moveOut);
	}
	
	public List<MoveOutEntry> getActiveMoveOuts()
	{
		return m_lActiveMoveOuts;
	}
	
	public void addPayee(PayeeEntry payee)
	{
		if (null == m_lActivePayees)
		{
			m_lActivePayees = new ArrayList<PayeeEntry>();
		}
		m_lActivePayees.add(payee);
	}
	
	public List<PayeeEntry> getActivePayees()
	{
		return m_lActivePayees;
	}
	
	public void addBalance(BalanceEntry balance)
	{
		if (null == m_lActiveBalances)
		{
			m_lActiveBalances = new ArrayList<BalanceEntry>();
		}
		m_lActiveBalances.add(balance);
	}
	
	public List<BalanceEntry> getActiveBalances()
	{
		return m_lActiveBalances;
	}
	
	public void addPayment(PaymentEntry payment)
	{
		if (null == m_lActivePayments)
		{
			m_lActivePayments = new ArrayList<PaymentEntry>();
		}
		m_lActivePayments.add(payment);
	}
	
	public List<PaymentEntry> getActivePayments()
	{
		return m_lActivePayments;
	}
	
	public List<PaymentEntry> getActivePayments(PayeeEntry payeeEntry)
	{
		List<PaymentEntry> lResult = new ArrayList<PaymentEntry>();
		List<PaymentEntry> lActivePayments = getActivePayments();
		if (null != lActivePayments)
		{
			for (PaymentEntry payment : lActivePayments)
			{
				if (payeeEntry.getPayeeName().equals(payment.getAssociatedPayeeEntry().getPayeeName()))
				{
					lResult.add(payment);
				}
			}
		}
		return lResult;
	}

	public void addDeposit(DepositEntry deposit)
	{
		if (null == m_lActiveDeposits)
		{
			m_lActiveDeposits = new ArrayList<DepositEntry>();
		}
		m_lActiveDeposits.add(deposit);
	}
	
	public List<DepositEntry> getActiveDeposits()
	{
		return m_lActiveDeposits;
	}
	
	public void addRemove(RemoveEntry remove)
	{
		if (null == m_lActiveRemoves)
		{
			m_lActiveRemoves = new ArrayList<RemoveEntry>();
		}
		m_lActiveRemoves.add(remove);
	}
	
	public List<RemoveEntry> getActiveRemoves()
	{
		return m_lActiveRemoves;
	}
	
	public void addFlush(FlushEntry flush)
	{
		if (null == m_lActiveFlushes)
		{
			m_lActiveFlushes = new ArrayList<FlushEntry>();
		}
		m_lActiveFlushes.add(flush);
	}
	
	public List<FlushEntry> getActiveFlushes()
	{
		return m_lActiveFlushes;
	}
	
	public void add(LedgerEntryVisitor visitor)
	{
		if (null == m_lVisitors)
		{
			m_lVisitors = new ArrayList<LedgerEntryVisitor>();
		}
		m_lVisitors.add(visitor);
	}
	
	public LedgerEntryVisitor getVisitor(Class clazz)
	{
		if (null != m_lVisitors)
		{
			for (LedgerEntryVisitor candidate : m_lVisitors)
			{
				if (candidate.getClass().isAssignableFrom(clazz))
				{
					return candidate;
				}
			}
			m_lVisitors = new ArrayList<LedgerEntryVisitor>();
		}
		return null;
	}
	
	public String toString(PayeeEntry payeeEntry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (null != m_lActivePayees)
		{
			for (PayeeEntry payee : m_lActivePayees)
			{
				if (payeeEntry.getPayeeName().equals(payee.getPayeeName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Activated as a Payee\n");
				}
			}
		}
		if (null != m_lActiveRemoves)
		{
			for (RemoveEntry remove : m_lActiveRemoves)
			{
				if (payeeEntry.getPayeeName().equals(remove.getTargetName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("De-Activated as a Payee (Removed)\n");
				}
			}
		}
		if (null != m_lActivePayments)
		{
			for (PaymentEntry payment : m_lActivePayments)
			{
				if (payeeEntry.getPayeeName().equals(payment.getAssociatedPayeeEntry().getPayeeName()))
				{
					PaymentsVisitor paymentsVisitor = (PaymentsVisitor)getVisitor(PaymentsVisitor.class);
					if (null != paymentsVisitor)
					{
						List<PaymentDetails> lDetails = paymentsVisitor.getPaymentDetails(payment);
						if (null != lDetails)
						{
							sb.append(StringUtil.getIndent(iIndent + 2)).append("Payment: Total: ").append(payment.getAmount().toString()).append(", Service Period: ").append(Entry.toString(payment.getStartDate())).append(" to ").append(Entry.toString(payment.getEndDate())).append("\n");
							for (PaymentDetails details : lDetails)
							{
								sb.append(StringUtil.getIndent(iIndent + 3)).append("From: ").append(details.getTenantEntry().getTenantName()).append(": ").append(details.getPercentage()).append("%, Amount: ").append(details.getAmountDisplayName()).append("\n");
							}
						}
					}
				}
			}
		}
		if (0 != sb.length())
		{
			StringBuilder sbWrapper = new StringBuilder();
			StringUtil.toString(sbWrapper, "Date", DateUtil.getTime(Entry.STANDARD_DATEFORMAT, getDate()), iIndent + 1);
			sbWrapper.append(sb.toString());
			return sbWrapper.toString();
		}
		return "";
	}

	
	public String toString(TenantEntry tenantEntry, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (null != m_lActiveTenants)
		{
			for (TenantEntry tenant : m_lActiveTenants)
			{
				if (tenantEntry.getTenantName().equals(tenant.getTenantName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Activated as a Tenant\n");
				}
			}
		}
		if (null != m_lActiveMoveOuts)
		{
			for (MoveOutEntry moveOut : m_lActiveMoveOuts)
			{
				if (tenantEntry.getTenantName().equals(moveOut.getTenantName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("De-Activated as a Tenant (Move Out)\n");
				}
			}
		}
		if (null != m_lActiveRemoves)
		{
			for (RemoveEntry remove : m_lActiveRemoves)
			{
				if (tenantEntry.getTenantName().equals(remove.getTargetName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Removed as a Tenant (Removed)\n");
				}
			}
		}
		if (null != m_lActiveBalances)
		{
			for (BalanceEntry balance : m_lActiveBalances)
			{
				if (tenantEntry.getTenantName().equals(balance.getTenantName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Balance set to: ").append(balance.getBalance().toString()).append("\n");
				}
			}
		}
		if (null != m_lActiveFlushes)
		{
			for (FlushEntry flush : m_lActiveFlushes)
			{
				if ((tenantEntry.getTenantName().equals(flush.getFromTenantName())) ||
					(tenantEntry.getTenantName().equals(flush.getToTenantName())))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Flush: From: ").append(flush.getFromTenantName()).append(" to ").append(flush.getToTenantName()).append("\n");
				}
			}
		}
		if (null != m_lActivePayments)
		{
			for (PaymentEntry payment : m_lActivePayments)
			{
				PaymentsVisitor paymentsVisitor = (PaymentsVisitor)getVisitor(PaymentsVisitor.class);
				if (null != paymentsVisitor)
				{
					PaymentDetails details = paymentsVisitor.getPaymentDetails(payment, tenantEntry);
					if (null != details)
					{
						int iPayeeCount = paymentsVisitor.getPaymentPayeeCount(payment);
						sb.append(StringUtil.getIndent(iIndent + 2)).append("Paid: ").append(payment.getPayeeName()).append(": $").append(details.getAmountDisplayName());
						sb.append(" of $").append(payment.getAmountDisplayName()).append(": Tenants Paying: ").append(iPayeeCount).append(", Service Period: ");
						sb.append(details.getPercentage()).append("% of ").append(Entry.getDateString(payment.getStartDate())).append(" - ").append(Entry.getDateString(payment.getEndDate())).append("\n");
					}
				}
			}
		}
		if (null != m_lActiveDeposits)
		{
			for (DepositEntry deposit : m_lActiveDeposits)
			{
				if (tenantEntry.getTenantName().equals(deposit.getTenantName()))
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Deposited: ").append(deposit.getAmount().toString()).append("\n");
				}
			}
		}
		if (0 != sb.length())
		{
			BalancesVisitor balancesVisitor = (BalancesVisitor)getVisitor(BalancesVisitor.class);
			if (null != balancesVisitor)
			{
				MoneyInteger balance = balancesVisitor.getBalance(tenantEntry);
				if (null != balance)
				{
					sb.append(StringUtil.getIndent(iIndent + 2)).append("Balance: ").append(balance.toString()).append("\n");
				}
			}
			StringBuilder sbWrapper = new StringBuilder();
			StringUtil.toString(sbWrapper, "Date", DateUtil.getTime(Entry.STANDARD_DATEFORMAT, getDate()), iIndent + 1);
			sbWrapper.append(sb.toString());
			return sbWrapper.toString();
		}
		return "";
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getIndent(iIndent)).append(getClass().getSimpleName()).append("\n");
		StringUtil.toString(sb, "Date", DateUtil.getTime(Entry.STANDARD_DATEFORMAT, getDate()), iIndent + 1);
		if (null != m_lActiveTenants)
		{
			StringUtil.toString(sb, "Tenants", m_lActiveTenants.size(), iIndent + 1);
			for (TenantEntry tenant : m_lActiveTenants)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Tenant: ").append(tenant.getTenantName()).append("\n");
			}
		}
		if (null != m_lActiveMoveOuts)
		{
			StringUtil.toString(sb, "MoveOuts", m_lActiveMoveOuts.size(), iIndent + 1);
			for (MoveOutEntry moveOut : m_lActiveMoveOuts)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Tenant: ").append(moveOut.getTenantName()).append("\n");
			}
		}
		if (null != m_lActivePayees)
		{
			StringUtil.toString(sb, "Payees", m_lActivePayees.size(), iIndent + 1);
			for (PayeeEntry payee : m_lActivePayees)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Payee: ").append(payee.getPayeeName()).append("\n");
			}
		}
		if (null != m_lActiveBalances)
		{
			StringUtil.toString(sb, "Balances", m_lActiveBalances.size(), iIndent + 1);
			for (BalanceEntry balance : m_lActiveBalances)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Balance: ").append(balance.getTenantName()).append(", Amount: ").append(balance.getBalance()).append("\n");
			}
		}
		if (null != m_lActivePayments)
		{
			StringUtil.toString(sb, "Payments", m_lActivePayments.size(), iIndent + 1);
			for (PaymentEntry payment : m_lActivePayments)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Payment: ").append(payment.getPayeeName()).append(", Amount: ").append(payment.getAmount()).append(", Start: ").append(Entry.toString(payment.getStartDate())).append(", End: ").append(Entry.toString(payment.getEndDate())).append("\n");
			}
		}
		if (null != m_lActiveDeposits)
		{
			StringUtil.toString(sb, "Deposits", m_lActiveDeposits.size(), iIndent + 1);
			for (DepositEntry deposit : m_lActiveDeposits)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Deposit: ").append(deposit.getTenantName()).append(", Amount: ").append(deposit.getAmount()).append("\n");
			}
		}
		if (null != m_lActiveFlushes)
		{
			StringUtil.toString(sb, "Flushes", m_lActiveFlushes.size(), iIndent + 1);
			for (FlushEntry flush : m_lActiveFlushes)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Flush: From: ").append(flush.getFromTenantName()).append(" to ").append(flush.getToTenantName()).append("\n");
			}
		}
		if (null != m_lActiveRemoves)
		{
			StringUtil.toString(sb, "Removes", m_lActiveRemoves.size(), iIndent + 1);
			for (RemoveEntry remove : m_lActiveRemoves)
			{
				sb.append(StringUtil.getIndent(iIndent + 2)).append("Target: ").append(remove.getTargetName()).append("\n");
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
	
	@Override
	public int hashCode()
	{
		int iCode = 37;
		iCode = iCode * 37 + (getActiveTenants() != null ? getActiveTenants().hashCode() : 0);
		iCode = iCode * 37 + (getActivePayees() != null ? getActivePayees().hashCode() : 0);
		iCode = iCode * 37 + (getActiveBalances() != null ? getActiveBalances().hashCode() : 0);
		iCode = iCode * 37 + (getActivePayments() != null ? getActivePayments().hashCode() : 0);
		iCode = iCode * 37 + (getActiveDeposits() != null ? getActiveDeposits().hashCode() : 0);
		iCode = iCode * 37 + (getActiveMoveOuts() != null ? getActiveMoveOuts().hashCode() : 0);
		iCode = iCode * 37 + (getActiveRemoves() != null ? getActiveRemoves().hashCode() : 0);
		iCode = iCode * 37 + (getActiveFlushes() != null ? getActiveFlushes().hashCode() : 0);
		iCode = iCode * 37 + (getDate() != null ? getDate().hashCode() : 0);
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof LedgerEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if ((CollectionUtil.equals(getActiveTenants(), ((LedgerEntry)that).getActiveTenants())) &&
					(CollectionUtil.equals(getActivePayees(), ((LedgerEntry)that).getActivePayees())) &&
					(CollectionUtil.equals(getActiveBalances(), ((LedgerEntry)that).getActiveBalances())) &&
					(CollectionUtil.equals(getActivePayments(), ((LedgerEntry)that).getActivePayments())) &&
					(CollectionUtil.equals(getActiveDeposits(), ((LedgerEntry)that).getActiveDeposits())) &&
					(CollectionUtil.equals(getActiveMoveOuts(), ((LedgerEntry)that).getActiveMoveOuts())) &&
					(CollectionUtil.equals(getActiveRemoves(), ((LedgerEntry)that).getActiveRemoves())) &&
					(CollectionUtil.equals(getActiveFlushes(), ((LedgerEntry)that).getActiveFlushes())) &&
					(ObjectUtil.areReferencesEqual(getDate(), ((LedgerEntry)that).getDate())))			
				{
					return true;
				}
			}
		}
		return false;
	}
}
