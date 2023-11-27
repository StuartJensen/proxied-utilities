package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class FlushEntry
	extends RegisterBaseEntry
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
	
	public String buildRegisterReport(int iIndent)
	{
		return toString(iIndent);
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.buildLedgerReport(iIndent));
		sb.append(", From: ").append(getFromTenantName());
		sb.append(", To: ").append(getToTenantName()).append("\n");
		return sb.toString();
	}

	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("From: ").append(getFromTenantName().toString()).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("To: ").append(getToTenantName().toString()).append("\n");
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
		if (null != getFromTenantName())
		{
			iCode = HashUtil.hash(iCode, getFromTenantName());
		}
		if (null != getToTenantName())
		{
			iCode = HashUtil.hash(iCode, getToTenantName());
		}
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
