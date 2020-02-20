package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.ObjectUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class BalanceEntry
	extends Entry
{
	private String m_strTenantName;
	private TenantEntry m_associatedTenant;
	private MoneyInteger m_iBalance;
	
	public BalanceEntry(String strDate, String strTenantName, String strBalance)
		throws ParseException, InvalidParameterException, NumberFormatException
	{
		super(strDate, Verb.BALANCE);
		m_strTenantName = strTenantName;
		m_iBalance = new MoneyInteger(strBalance);
	}
	
	public MoneyInteger getBalance()
	{
		return m_iBalance;
	}
	
	public String getTenantName()
	{
		return m_strTenantName;
	}
	
	public void setAssociatedTenantEntry(TenantEntry associatedTenant)
	{
		m_associatedTenant = associatedTenant;
	}
	
	public TenantEntry getAssociatedTenantEntry()
	{
		return m_associatedTenant;
	}

	@Override
	public int compareTo(Entry that)
	{
		int iResult = super.compareTo(that);
		if (0 == iResult)
		{	// Always place BALANCE entries before equivalent non-BALANCE entries
			if (!(that instanceof BalanceEntry))
			{
				return -1;
			}
		}
		return iResult;
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "Balance", getBalance().toString(), iIndent + 1);
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
		int iCode = super.hashCode();
		if (null != getTenantName())
		{
			iCode = iCode * 37 + getTenantName().hashCode();
		}
		if (null != getBalance())
		{
			iCode = iCode * 37 + getBalance().hashCode();
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof BalanceEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if ((StringUtil.areReferencesEqual(this.getTenantName(), ((BalanceEntry)that).getTenantName())) &&
					(ObjectUtil.areReferencesEqual(this.getBalance(), ((BalanceEntry)that).getBalance())))
				{
					return true;
				}
			}
		}
		return false;
	}
}
