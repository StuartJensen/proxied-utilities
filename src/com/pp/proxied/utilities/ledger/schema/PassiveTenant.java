package com.pp.proxied.utilities.ledger.schema;

import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.StringUtil;

public class PassiveTenant
{
	private TenantEntry m_tenant;
	private MoneyInteger m_balance;
	
	public PassiveTenant(TenantEntry tenant)
	{
		m_tenant = tenant;
	}
	
	public PassiveTenant(PassiveTenant source)
	{
		if (null != source)
		{
			m_tenant = source.getTenant();
		}
	}
	
	public TenantEntry getTenant()
	{
		return m_tenant;
	}
	
	public void setBalance(MoneyInteger balance)
	{
		m_balance = balance;
	}
	
	
	public MoneyInteger getBalance()
	{
		return m_balance;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getSpaces(iIndent)).append("PassiveTenant: ");
		sb.append(m_tenant.getTenantName());
		sb.append(", Balance: " + m_balance.toString());
		return sb.toString();
	}
}
