package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import internal.atlaslite.jcce.util.HashCodeUtil;
import internal.atlaslite.jcce.util.ObjectUtil;
import internal.atlaslite.jcce.util.StringUtil;

public class DepositEntry
	extends Entry
{
	private String m_strTenantName;
	private TenantEntry m_associatedTenant;
	private MoneyInteger m_iAmount;
	
	public DepositEntry(String strDate, String strTenantName, String strAmount)
		throws ParseException, InvalidParameterException, NumberFormatException
	{
		super(strDate, Verb.DEPOSIT);
		m_strTenantName = strTenantName;
		m_iAmount = new MoneyInteger(strAmount);
	}
	
	public MoneyInteger getAmount()
	{
		return m_iAmount;
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
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "Amount", getAmount().toString(), iIndent + 1);
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
			iCode = HashCodeUtil.hash(iCode, getTenantName());
		}
		if (null != getAmount())
		{
			iCode = HashCodeUtil.hash(iCode, getAmount());
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof DepositEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if ((StringUtil.areReferencesEqual(this.getTenantName(), ((DepositEntry)that).getTenantName())) &&
					(ObjectUtil.areReferencesEqual(this.getAmount(), ((DepositEntry)that).getAmount())))
				{
					return true;
				}
			}
		}
		return false;
	}
}
