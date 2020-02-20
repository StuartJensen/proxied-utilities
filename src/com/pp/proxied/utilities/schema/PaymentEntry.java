package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Calendar;

import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.ObjectUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class PaymentEntry
	extends Entry
{
	private String m_strPayeeName;
	private PayeeEntry m_associatedPayee;
	private MoneyInteger m_iAmount;
	private Calendar m_startDate;
	private Calendar m_endDate;
	
	public PaymentEntry(String strDate, String strPayeeName, String strAmount, String strStartDate, String strEndDate)
		throws ParseException, InvalidParameterException, NumberFormatException
	{
		super(strDate, Verb.PAYMENT);
		m_strPayeeName = strPayeeName;
		m_iAmount = new MoneyInteger(strAmount);
		m_startDate = parseDate(strStartDate);
		m_endDate = parseDate(strEndDate);
	}
	
	public String getPayeeName()
	{
		return m_strPayeeName;
	}
	
	public void setAssociatedPayeeEntry(PayeeEntry associatedPayee)
	{
		m_associatedPayee = associatedPayee;
	}
	
	public PayeeEntry getAssociatedPayeeEntry()
	{
		return m_associatedPayee;
	}
	
	public MoneyInteger getAmount()
	{
		return m_iAmount;
	}
	
	public String getAmountDisplayName()
	{
		if (null != m_iAmount)
		{
			return m_iAmount.toString();
		}
		return "0";
	}
	
	public Calendar getStartDate()
	{
		return m_startDate;
	}
	
	public Calendar getEndDate()
	{
		return m_endDate;
	}
	
	public MoneyInteger getAmountPerDay()
	{
		return getAmount().divide(new MoneyInteger(getDaysInPeriod()));
	}
	
	public int getDaysInPeriod()
	{
		return Entry.getDaysInPeriod(getStartDate(), getEndDate());
	}
	
	public boolean inServiceOn(Calendar date)
	{
		return ((0 <= date.compareTo(getStartDate())) && (0 >= date.compareTo(getEndDate())));
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		StringUtil.toString(sb, "Payee", getPayeeName(), iIndent + 1);
		StringUtil.toString(sb, "Amount", getAmount(), iIndent + 1);
		StringUtil.toString(sb, "Start Service Date", DateUtil.getTime(STANDARD_DATEFORMAT, getStartDate()), iIndent + 1);
		StringUtil.toString(sb, "End Service Date", DateUtil.getTime(STANDARD_DATEFORMAT, getEndDate()), iIndent + 1);
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
		iCode = iCode * 37 + (getPayeeName() != null ? getPayeeName().hashCode() : 0);
		iCode = iCode * 37 + (getAmount() != null ? getAmount().hashCode() : 0);
		iCode = iCode * 37 + (getStartDate() != null ? getStartDate().hashCode() : 0);
		iCode = iCode * 37 + (getEndDate() != null ? getEndDate().hashCode() : 0);
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (super.equals(that))
		{
			if (that instanceof PaymentEntry)
			{
				if (this == that)
				{	// Same instance
					return true;
				}
				if ((StringUtil.areReferencesEqual(this.getPayeeName(), ((PaymentEntry)that).getPayeeName())) &&
					(ObjectUtil.areReferencesEqual(this.getAmount(), ((PaymentEntry)that).getAmount())) &&
					(ObjectUtil.areReferencesEqual(this.getStartDate(), ((PaymentEntry)that).getStartDate())) &&
					(ObjectUtil.areReferencesEqual(this.getEndDate(), ((PaymentEntry)that).getEndDate())))
				{
					return true;
				}
			}
		}
		return false;
	}
}