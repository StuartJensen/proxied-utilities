package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Calendar;

import com.pp.proxied.utilities.util.StringUtil;

public class RemoveEntry
	extends Entry
{
	private String m_strTargetName;
	private Entry m_associatedTarget;
	
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
	
	public void setAssociatedTargetEntry(Entry associatedTarget)
	{
		m_associatedTarget = associatedTarget;
	}
	
	public Entry getAssociatedTargetEntry()
	{
		return m_associatedTarget;
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "Target", getTargetName(), iIndent + 1);
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
		iCode = iCode * 37 + (getTargetName() != null ? getTargetName().hashCode() : 0);
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
