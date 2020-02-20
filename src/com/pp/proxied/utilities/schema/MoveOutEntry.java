package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.StringUtil;

public class MoveOutEntry
	extends Entry
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
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "Tenant", getTenantName(), iIndent + 1);
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
		iCode = iCode * 37 + (getTenantName() != null ? getTenantName().hashCode() : 0);
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
