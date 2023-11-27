package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Calendar;

import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.comparators.TenantEntryByTenantName;
import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class TenantEntry
	extends RegisterBaseEntry
{
	public static final String LANDLORD_TENANT_NAME = "LANDLORD";
	
	private String m_strTenantName;
	private static TenantEntry m_landlordInstance;
	private static MoneyInteger m_landlordBalance;
	
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
			m_landlordBalance = MoneyInteger.ZERO;
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
	
	public static MoneyInteger addToLandlordBalance(MoneyInteger additional)
	{
		if (null == m_landlordBalance)
		{
			m_landlordBalance = MoneyInteger.ZERO;
		}
		m_landlordBalance = m_landlordBalance.plus(additional);
		return m_landlordBalance;
	}
	
	public static MoneyInteger getLandlordBalance()
	{
		return m_landlordBalance;
	}
	
	public String getTenantName()
	{
		return m_strTenantName;
	}
	
	public String buildRegisterReport(int iIndent)
	{
		return toString(iIndent);
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.buildLedgerReport(iIndent));
		sb.append(", ").append(getTenantName()).append("\n");
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
	public int compareTo(RegisterBaseEntry that)
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