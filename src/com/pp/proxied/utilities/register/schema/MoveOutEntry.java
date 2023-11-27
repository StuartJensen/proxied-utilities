package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class MoveOutEntry
	extends RegisterBaseEntry
{
	private String m_strTenantName;
	private TenantEntry m_associatedTenant;
	
	public MoveOutEntry(String strDate, String strTenantName)
		throws ParseException, InvalidParameterException
	{
		super(strDate, Verb.MOVEOUT);
		m_strTenantName = strTenantName;
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
	
	public String buildRegisterReport(int iIndent)
	{
		return toString(iIndent);
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.buildLedgerReport(iIndent));
		sb.append(", Tenant: ").append(getTenantName()).append("\n");
		return sb.toString();
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Tenant: ").append(getTenantName()).append("\n");
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
			iCode = HashUtil.hash(iCode, getTenantName());
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof MoveOutEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if (StringUtil.areReferencesEqual(this.getTenantName(), ((MoveOutEntry)that).getTenantName()))
				{
					return true;
				}
			}
		}
		return false;
	}
}
