package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class PayeeEntry
	extends RegisterBaseEntry
{
	private String m_strPayeeName;
	private String m_strAbbreviation;
	
	public PayeeEntry(String strDate, String strPayeeName, String strAbbreviation)
		throws ParseException, InvalidParameterException
	{
		super(strDate, Verb.PAYEE);
		m_strPayeeName = strPayeeName;
		m_strAbbreviation = strAbbreviation;
	}
	
	public String getPayeeName()
	{
		return m_strPayeeName;
	}
	
	public String getPayeeAbbreviation()
	{
		return m_strAbbreviation;
	}
	
	public String buildRegisterReport(int iIndent)
	{
		return toString(iIndent);
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.buildLedgerReport(iIndent));
		sb.append(", Payee: ").append(getPayeeName()).append("\n");
		return sb.toString();
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Payee: ").append(getPayeeName()).append("\n");
		return sb.toString();
	}
	
	@Override
	public int hashCode()
	{
		int iCode = super.hashCode();
		if (null != getPayeeName())
		{
			iCode = HashUtil.hash(iCode, getPayeeName());
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof PayeeEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if (StringUtil.areReferencesEqual(this.getPayeeName(), ((PayeeEntry)that).getPayeeName()))
				{
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
}
