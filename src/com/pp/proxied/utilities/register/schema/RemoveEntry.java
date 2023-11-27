package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Calendar;

import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class RemoveEntry
	extends RegisterBaseEntry
{
	private String m_strTargetName;
	private RegisterBaseEntry m_associatedTarget;
	
	public RemoveEntry(Calendar date, String strTargetName)
		throws InvalidParameterException
	{
		super(date, Verb.REMOVE);
		m_strTargetName = strTargetName;
	}
	
	public RemoveEntry(String strDate, String strTargetName)
		throws ParseException, InvalidParameterException
	{
		super(strDate, Verb.REMOVE);
		m_strTargetName = strTargetName;
	}
	
	public String getTargetName()
	{
		return m_strTargetName;
	}
	
	public void setAssociatedTargetEntry(RegisterBaseEntry associatedTarget)
	{
		m_associatedTarget = associatedTarget;
	}
	
	public RegisterBaseEntry getAssociatedTargetEntry()
	{
		return m_associatedTarget;
	}
	
	public String buildRegisterReport(int iIndent)
	{
		return toString(iIndent);
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.buildLedgerReport(iIndent));
		sb.append(", Target: ").append(getTargetName()).append("\n");
		return sb.toString();
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Target: ").append(getTargetName()).append("\n");
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
		if (null != getTargetName())
		{
			iCode = HashUtil.hash(iCode, getTargetName());
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof RemoveEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if (StringUtil.areReferencesEqual(this.getTargetName(), ((RemoveEntry)that).getTargetName()))
				{
					return true;
				}
			}
		}
		return false;
	}

}
