package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Calendar;

import com.pp.proxied.utilities.schema.comparators.TenantEntryByTenantName;

import internal.atlaslite.jcce.util.HashCodeUtil;
import internal.atlaslite.jcce.util.StringUtil;

public class TenantEntry
	extends Entry
{
	public static final String LANDLORD_TENANT_NAME = "LANDLORD";
	
	private String m_strTenantName;
	private static TenantEntry m_landlordInstance;
	
	public TenantEntry(Calendar date, String strTenantName)
		throws InvalidParameterException
	{
		super(date, Verb.TENANT);
		m_strTenantName = strTenantName;
	}
	
	public TenantEntry(String strDate, String strTenantName)
		throws ParseException, InvalidParameterException
	{
		super(strDate, Verb.TENANT);
		m_strTenantName = strTenantName;
	}
	
	public static TenantEntry createLandLordInstance(Calendar date)
	{
		if (null == m_landlordInstance)
		{
			m_landlordInstance = new TenantEntry(date, LANDLORD_TENANT_NAME);
		}
		return m_landlordInstance;
	}
	
	public static TenantEntry getLandLordInstance()
	{
		return m_landlordInstance;
	}
	
	public boolean isLandlordInstance()
	{
		return (this == m_landlordInstance);
	}
	
	public String getTenantName()
	{
		return m_strTenantName;
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
		if (null != getTenantName())
		{
			iCode = HashCodeUtil.hash(iCode, getTenantName());
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof TenantEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if (StringUtil.areReferencesEqual(this.getTenantName(), ((TenantEntry)that).getTenantName()))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public int compareTo(Entry that)
	{
		int iResult = super.compareTo(that);
		if (0 == iResult)
		{
			if (!(that instanceof TenantEntry))
			{
				iResult = 1;
			}
			iResult = new TenantEntryByTenantName().compare(this, (TenantEntry)that);
		}
		return iResult;
	}
}