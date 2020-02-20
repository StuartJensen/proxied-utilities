package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.StringUtil;

public class FlushEntry
	extends Entry
{
	private String m_strFromTenantName;
	private String m_strToTenantName;
	private TenantEntry m_associatedFromTenant;
	private TenantEntry m_associatedToTenant;
	
	public FlushEntry(String strDate, String strFromTenantName, String strToTenantName)
		throws ParseException, InvalidParameterException, NumberFormatException
	{
		super(strDate, Verb.FLUSH);
		m_strFromTenantName = strFromTenantName;
		m_strToTenantName = strToTenantName;
	}
	
	public String getFromTenantName()
	{
		return m_strFromTenantName;
	}
	
	public String getToTenantName()
	{
		return m_strToTenantName;
	}
	
	public void setAssociatedFromTenantEntry(TenantEntry associatedTenant)
	{
		m_associatedFromTenant = associatedTenant;
	}
	
	public TenantEntry getAssociatedFromTenantEntry()
	{
		return m_associatedFromTenant;
	}
	
	public void setAssociatedToTenantEntry(TenantEntry associatedTenant)
	{
		m_associatedToTenant = associatedTenant;
	}
	
	public TenantEntry getAssociatedToTenantEntry()
	{
		return m_associatedToTenant;
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "From", getFromTenantName().toString(), iIndent + 1);
		StringUtil.toString(sb, "To", getToTenantName().toString(), iIndent + 1);
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
		iCode = iCode * 37 + (getFromTenantName() != null ? getFromTenantName().hashCode() : 0);
		iCode = iCode * 37 + (getToTenantName() != null ? getToTenantName().hashCode() : 0);
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof FlushEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if ((StringUtil.areReferencesEqual(this.getFromTenantName(), ((FlushEntry)that).getFromTenantName())) &&
					(StringUtil.areReferencesEqual(this.getToTenantName(), ((FlushEntry)that).getToTenantName())))
				{
					return true;
				}
			}
		}
		return false;
	}
}
