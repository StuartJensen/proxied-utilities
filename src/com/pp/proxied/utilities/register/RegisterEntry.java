package com.pp.proxied.utilities.register;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.CollectionUtil;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.ObjectUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class RegisterEntry
{
	private int m_iNthEntryInLedger;
	private List<TenantEntry> m_lRegistryTenants;
	private List<PayeeEntry> m_lRegistryPayees;
	private List<BalanceEntry> m_lRegistryBalances;
	private List<PaymentEntry> m_lRegistryPayments;
	private List<DepositEntry> m_lRegistryDeposits;
	private List<MoveOutEntry> m_lRegistryMoveOuts;
	private List<RemoveEntry> m_lRegistryRemoves;
	private List<FlushEntry> m_lRegistryFlushes;
	private Calendar m_date;
	
	public RegisterEntry(Calendar date, int iNthEntryInLedger)
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
		return DateUtil.getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, m_date.getTimeInMillis());
	}
	
	public int getNthEntryInledger()
	{
		return m_iNthEntryInLedger;
	}
	
	public boolean isBefore(Calendar that)
	{
		return (0 > RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOnOrBefore(Calendar that)
	{
		return (0 >= RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isAfter(Calendar that)
	{
		return (0 < RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOnOrAfter(Calendar that)
	{
		return (0 <= RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOn(Calendar that)
	{
		return (0 == RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public static boolean isBefore(Calendar source, Calendar that)
	{
		return (0 > RegisterBaseEntry.compareMonthDayYear(source, that));
	}
	
	public static boolean isOnOrBefore(Calendar source, Calendar that)
	{
		return (0 >= RegisterBaseEntry.compareMonthDayYear(source, that));
	}
	
	public static boolean isAfter(Calendar source, Calendar that)
	{
		return (0 < RegisterBaseEntry.compareMonthDayYear(source, that));
	}
	
	public static boolean isOnOrAfter(Calendar source, Calendar that)
	{
		return (0 <= RegisterBaseEntry.compareMonthDayYear(source, that));
	}
	
	public static boolean isOn(Calendar source, Calendar that)
	{
		return (0 == RegisterBaseEntry.compareMonthDayYear(source, that));
	}

	public void addRegistryTenant(TenantEntry tenant)
	{
		if (null == m_lRegistryTenants)
		{
			m_lRegistryTenants = new ArrayList<TenantEntry>();
		}
		m_lRegistryTenants.add(tenant);
	}
	
	public List<TenantEntry> getRegistryTenants()
	{
		return m_lRegistryTenants;
	}

	public void addRegistryMoveOut(MoveOutEntry moveOut)
	{
		if (null == m_lRegistryMoveOuts)
		{
			m_lRegistryMoveOuts = new ArrayList<MoveOutEntry>();
		}
		m_lRegistryMoveOuts.add(moveOut);
	}
	
	public List<MoveOutEntry> getRegistryMoveOuts()
	{
		return m_lRegistryMoveOuts;
	}
	
	public void addRegistryPayee(PayeeEntry payee)
	{
		if (null == m_lRegistryPayees)
		{
			m_lRegistryPayees = new ArrayList<PayeeEntry>();
		}
		m_lRegistryPayees.add(payee);
	}
	
	public List<PayeeEntry> getRegistryPayees()
	{
		return m_lRegistryPayees;
	}
	
	public void addRegistryBalance(BalanceEntry balance)
	{
		if (null == m_lRegistryBalances)
		{
			m_lRegistryBalances = new ArrayList<BalanceEntry>();
		}
		m_lRegistryBalances.add(balance);
	}
	
	public List<BalanceEntry> getRegistryBalances()
	{
		return m_lRegistryBalances;
	}
	
	public void addRegistryPayment(PaymentEntry payment)
	{
		if (null == m_lRegistryPayments)
		{
			m_lRegistryPayments = new ArrayList<PaymentEntry>();
		}
		m_lRegistryPayments.add(payment);
	}
	
	public List<PaymentEntry> getRegistryPayments()
	{
		return m_lRegistryPayments;
	}
	
	public List<PaymentEntry> getRegistryPayments(PayeeEntry payeeEntry)
	{
		List<PaymentEntry> lResult = new ArrayList<PaymentEntry>();
		List<PaymentEntry> lActivePayments = getRegistryPayments();
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

	public void addRegistryDeposit(DepositEntry deposit)
	{
		if (null == m_lRegistryDeposits)
		{
			m_lRegistryDeposits = new ArrayList<DepositEntry>();
		}
		m_lRegistryDeposits.add(deposit);
	}
	
	public List<DepositEntry> getRegistryDeposits()
	{
		return m_lRegistryDeposits;
	}
	
	public void addRegistryRemove(RemoveEntry remove)
	{
		if (null == m_lRegistryRemoves)
		{
			m_lRegistryRemoves = new ArrayList<RemoveEntry>();
		}
		m_lRegistryRemoves.add(remove);
	}
	
	public List<RemoveEntry> getRegistryRemoves()
	{
		return m_lRegistryRemoves;
	}
	
	public void addRegistryFlush(FlushEntry flush)
	{
		if (null == m_lRegistryFlushes)
		{
			m_lRegistryFlushes = new ArrayList<FlushEntry>();
		}
		m_lRegistryFlushes.add(flush);
	}
	
	public List<FlushEntry> getRegistryFlushes()
	{
		return m_lRegistryFlushes;
	}
		
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getSpaces(iIndent)).append(getClass().getSimpleName()).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Date: ").append(DateUtil.getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, getDate())).append("\n");
		if (null != m_lRegistryTenants)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Tenants: ").append(m_lRegistryTenants.size()).append("\n");
			for (TenantEntry tenant : m_lRegistryTenants)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Tenant: ").append(tenant.getTenantName()).append("\n");
			}
		}
		if (null != m_lRegistryMoveOuts)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("MoveOuts: ").append(m_lRegistryMoveOuts.size()).append("\n");
			for (MoveOutEntry moveOut : m_lRegistryMoveOuts)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Tenant: ").append(moveOut.getTenantName()).append("\n");
			}
		}
		if (null != m_lRegistryPayees)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Payees: ").append(m_lRegistryPayees.size()).append("\n");
			for (PayeeEntry payee : m_lRegistryPayees)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Payee: ").append(payee.getPayeeName()).append("\n");
			}
		}
		if (null != m_lRegistryBalances)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Balances: ").append(m_lRegistryBalances.size()).append("\n");
			for (BalanceEntry balance : m_lRegistryBalances)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Balance: ").append(balance.getTenantName()).append(", Amount: ").append(balance.getBalance()).append("\n");
			}
		}
		if (null != m_lRegistryPayments)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Payments: ").append(m_lRegistryPayments.size()).append("\n");
			for (PaymentEntry payment : m_lRegistryPayments)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Payment: ").append(payment.getPayeeName()).append(", Amount: ").append(payment.getAmount()).append(", Start: ").append(RegisterBaseEntry.toString(payment.getStartDate())).append(", End: ").append(RegisterBaseEntry.toString(payment.getEndDate())).append("\n");
			}
		}
		if (null != m_lRegistryDeposits)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Deposits: ").append(m_lRegistryDeposits.size()).append("\n");
			for (DepositEntry deposit : m_lRegistryDeposits)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Deposit: ").append(deposit.getTenantName()).append(", Amount: ").append(deposit.getAmount()).append("\n");
			}
		}
		if (null != m_lRegistryFlushes)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Flushes: ").append(m_lRegistryFlushes.size()).append("\n");
			for (FlushEntry flush : m_lRegistryFlushes)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Flush: From: ").append(flush.getFromTenantName()).append(" to ").append(flush.getToTenantName()).append("\n");
			}
		}
		if (null != m_lRegistryRemoves)
		{
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Removes: ").append(m_lRegistryRemoves.size()).append("\n");
			for (RemoveEntry remove : m_lRegistryRemoves)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append("Target: ").append(remove.getTargetName()).append("\n");
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
		int iCode = HashUtil.hash(8467, getRegistryTenants());
		iCode = HashUtil.hash(iCode, getRegistryPayees());
		iCode = HashUtil.hash(iCode, getRegistryBalances());
		iCode = HashUtil.hash(iCode, getRegistryPayments());
		iCode = HashUtil.hash(iCode, getRegistryDeposits());
		iCode = HashUtil.hash(iCode, getRegistryMoveOuts());
		iCode = HashUtil.hash(iCode, getRegistryRemoves());
		iCode = HashUtil.hash(iCode, getRegistryFlushes());
		iCode = HashUtil.hash(iCode, getDate());
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof RegisterEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if ((CollectionUtil.equals(getRegistryTenants(), ((RegisterEntry)that).getRegistryTenants())) &&
					(CollectionUtil.equals(getRegistryPayees(), ((RegisterEntry)that).getRegistryPayees())) &&
					(CollectionUtil.equals(getRegistryBalances(), ((RegisterEntry)that).getRegistryBalances())) &&
					(CollectionUtil.equals(getRegistryPayments(), ((RegisterEntry)that).getRegistryPayments())) &&
					(CollectionUtil.equals(getRegistryDeposits(), ((RegisterEntry)that).getRegistryDeposits())) &&
					(CollectionUtil.equals(getRegistryMoveOuts(), ((RegisterEntry)that).getRegistryMoveOuts())) &&
					(CollectionUtil.equals(getRegistryRemoves(), ((RegisterEntry)that).getRegistryRemoves())) &&
					(CollectionUtil.equals(getRegistryFlushes(), ((RegisterEntry)that).getRegistryFlushes())) &&
					(ObjectUtil.areReferencesEqual(getDate(), ((RegisterEntry)that).getDate())))			
				{
					return true;
				}
			}
		}
		return false;
	}
}
