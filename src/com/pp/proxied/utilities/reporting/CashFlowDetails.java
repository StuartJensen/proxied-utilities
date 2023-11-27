package com.pp.proxied.utilities.reporting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.StringUtil;

public class CashFlowDetails
{
	private int m_iYear;
	private Map<TenantEntry, MoneyInteger> m_tenantDeposits;
	private Map<TenantEntry, MoneyInteger> m_tenantPayments;
	private Map<PayeeEntry, MoneyInteger> m_payeePayments;
	private Map<PayeeEntry, MoneyInteger> m_payeeBilledPayments;
	private MoneyInteger m_sumPaymentsToAllPayees = MoneyInteger.ZERO;
	private MoneyInteger m_sumBilledPaymentsToAllPayees = MoneyInteger.ZERO;
	private MoneyInteger m_sumDepositsByAllTenants = MoneyInteger.ZERO;
	
	public CashFlowDetails(int iYear)
	{
		m_iYear = iYear;
		m_tenantDeposits = new HashMap<TenantEntry, MoneyInteger>();
		m_tenantPayments = new HashMap<TenantEntry, MoneyInteger>();
		m_payeePayments = new HashMap<PayeeEntry, MoneyInteger>();
		m_payeeBilledPayments = new HashMap<PayeeEntry, MoneyInteger>();
	}
	
	public boolean isEmpty()
	{
		return m_tenantDeposits.isEmpty() && m_tenantPayments.isEmpty() && m_payeePayments.isEmpty();
	}
	
	public MoneyInteger getExpenses()
	{
		return m_sumPaymentsToAllPayees;
	}
	
	public MoneyInteger getBilledExpenses()
	{
		return m_sumBilledPaymentsToAllPayees;
	}
	
	public MoneyInteger getIncome()
	{
		return m_sumDepositsByAllTenants;
	}

	public void addTenantDeposit(TenantEntry tenant, MoneyInteger amount)
	{
		MoneyInteger sum = m_tenantDeposits.get(tenant);
		if (null == sum)
		{
			sum = MoneyInteger.ZERO;
		}
		sum = sum.plus(amount);
		m_tenantDeposits.put(tenant, sum);
	}
	
	public void addTenantPayment(TenantEntry tenant, MoneyInteger amount)
	{
		MoneyInteger sum = m_tenantPayments.get(tenant);
		if (null == sum)
		{
			sum = MoneyInteger.ZERO;
		}
		sum = sum.plus(amount);
		m_tenantPayments.put(tenant, sum);
	}
	
	public void addPayeePayment(PayeeEntry payee, MoneyInteger amount)
	{
		MoneyInteger sum = m_payeePayments.get(payee);
		if (null == sum)
		{
			sum = MoneyInteger.ZERO;
		}
		sum = sum.plus(amount);
		m_payeePayments.put(payee, sum);
	}
	
	public void addBilledPayeePayment(PayeeEntry payee, MoneyInteger amount)
	{
		MoneyInteger sum = m_payeeBilledPayments.get(payee);
		if (null == sum)
		{
			sum = MoneyInteger.ZERO;
		}
		sum = sum.plus(amount);
		m_payeeBilledPayments.put(payee, sum);
	}	
	
	public void complete()
	{
		Iterator<PayeeEntry> iterPayees = m_payeePayments.keySet().iterator();
		while (iterPayees.hasNext())
		{
			PayeeEntry key = iterPayees.next();
			MoneyInteger value = m_payeePayments.get(key);
			m_sumPaymentsToAllPayees = m_sumPaymentsToAllPayees.plus(value);
		}

		Iterator<TenantEntry> iterDeposits = m_tenantDeposits.keySet().iterator();
		while (iterDeposits.hasNext())
		{
			TenantEntry key = iterDeposits.next();
			MoneyInteger value = m_tenantDeposits.get(key);
			m_sumDepositsByAllTenants = m_sumDepositsByAllTenants.plus(value);
		}
		
		iterPayees = m_payeeBilledPayments.keySet().iterator();
		while (iterPayees.hasNext())
		{
			PayeeEntry key = iterPayees.next();
			MoneyInteger value = m_payeeBilledPayments.get(key);
			m_sumBilledPaymentsToAllPayees = m_sumBilledPaymentsToAllPayees.plus(value);
		}

	}
	
	public String report()
	{
		StringBuilder sb = new StringBuilder();
		int iIndent = 0;
		// Report payments to payees
		sb.append("Cash Flow Report for Year: ").append(m_iYear).append("\n");
		
		sb.append(StringUtil.getSpaces(iIndent + 1));
		sb.append("Total paid to each payee:\n");
		Iterator<PayeeEntry> iter = m_payeePayments.keySet().iterator();
		while (iter.hasNext())
		{
			PayeeEntry key = iter.next();
			MoneyInteger value = m_payeePayments.get(key);
			sb.append(StringUtil.getSpaces(iIndent + 2));
			sb.append("Payee: ").append(key.getPayeeName());
			sb.append(": ").append(value.toString());
			sb.append("\n");
		}
		sb.append(StringUtil.getSpaces(2));
		sb.append("Total paid to all payees: ").append(m_sumPaymentsToAllPayees.toString()).append("\n");
		
		sb.append(StringUtil.getSpaces(iIndent + 1));
		sb.append("Total billed by each payee:\n");
		iter = m_payeeBilledPayments.keySet().iterator();
		while (iter.hasNext())
		{
			PayeeEntry key = iter.next();
			MoneyInteger value = m_payeeBilledPayments.get(key);
			sb.append(StringUtil.getSpaces(iIndent + 2));
			sb.append("Payee: ").append(key.getPayeeName());
			sb.append(": ").append(value.toString());
			sb.append("\n");
		}
		sb.append(StringUtil.getSpaces(2));
		sb.append("Total billed by all payees: ").append(m_sumBilledPaymentsToAllPayees.toString()).append("\n");
		sb.append(StringUtil.getSpaces(2));
		sb.append("Billed vs paid difference: ").append(m_sumBilledPaymentsToAllPayees.minus(m_sumPaymentsToAllPayees).toString()).append("\n");
		sb.append(StringUtil.getSpaces(3));
		sb.append("(Negative value means paid MORE than billed)\n");
		
		// Report payments by tenants
		sb.append(StringUtil.getSpaces(iIndent + 1));
		sb.append("Total paid by each tenant:\n");
		Iterator<TenantEntry> iterTenants = m_tenantPayments.keySet().iterator();
		while (iterTenants.hasNext())
		{
			TenantEntry key = iterTenants.next();
			MoneyInteger value = m_tenantPayments.get(key);
			sb.append(StringUtil.getSpaces(iIndent + 2));
			sb.append("Tenant: ").append(key.getTenantName());
			sb.append(": ").append(value.toString());
			sb.append("\n");
		}
		
		// Report tenant deposits
		sb.append(StringUtil.getSpaces(iIndent + 1));
		sb.append("Total deposited by each tenant:\n");
		Iterator<TenantEntry> iterDeposits = m_tenantDeposits.keySet().iterator();
		while (iterDeposits.hasNext())
		{
			TenantEntry key = iterDeposits.next();
			MoneyInteger value = m_tenantDeposits.get(key);
			sb.append(StringUtil.getSpaces(iIndent + 2));
			sb.append("Tenant: ").append(key.getTenantName());
			sb.append(": ").append(value.toString());
			sb.append("\n");
		}
		sb.append(StringUtil.getSpaces(iIndent + 2));
		sb.append("Total deposited by all tenants: ").append(m_sumDepositsByAllTenants.toString()).append("\n");

		sb.append(StringUtil.getSpaces(iIndent + 1));
		sb.append("CASH FLOW: ").append(m_sumDepositsByAllTenants.minus(m_sumPaymentsToAllPayees).toString()).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 2));
		sb.append("(Negative value means paid MORE than deposited)\n");
		sb.append("\n");
		return sb.toString();
	}
}
