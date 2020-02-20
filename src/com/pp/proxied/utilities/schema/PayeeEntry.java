package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;

import com.pp.proxied.utilities.util.StringUtil;

public class PayeeEntry
	extends Entry
{
	private String m_strPayeeName;
	
	public PayeeEntry(String strDate, String strPayeeName)
		throws ParseException, InvalidParameterException
	{
		super(strDate, Verb.PAYEE);
		m_strPayeeName = strPayeeName;
	}
	
	public String getPayeeName()
	{
		return m_strPayeeName;
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "Payee", getPayeeName(), iIndent + 1);
		return sb.toString();
	}
	
	@Override
	public int hashCode()
	{
		int iCode = super.hashCode();
		iCode = iCode * 37 + (getPayeeName() != null ? getPayeeName().hashCode() : 0);
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
