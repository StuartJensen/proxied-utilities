package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.Calendar;

import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.ObjectUtil;
import com.pp.proxied.utilities.util.StringUtil;

public class PaymentEntry
	extends RegisterBaseEntry
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
	
	public MoneyInteger getAmountPerDayInclusive()
	{
		return getAmount().divide(new MoneyInteger(getDaysInPeriodInclusive()));
	}
	
	public int getDaysInPeriod()
	{
		return RegisterBaseEntry.getDaysInPeriod(getStartDate(), getEndDate());
	}
	
	public int getDaysInPeriodInclusive()
	{
		return RegisterBaseEntry.getDaysInPeriodInclusive(getStartDate(), getEndDate());
	}
	
	public boolean inServiceOn(Calendar date)
	{
		return ((0 >= compareMonthDayYear(getStartDate(), date)) &&
				(0 <= compareMonthDayYear(getEndDate(), date)));
	}
	
   /**
    * Is {@code that} date on or after this entry's date?
    * 
    * @param that The date to test.
    * @return {@code true} iff {@code that} date is on or after this entry's date
    */
	public boolean isOnOrAfter(Calendar that)
	{
		return (0 <= compareMonthDayYear(getDate(), that));
	}
	
	
	@Override
	public Calendar getEarlier(Calendar candidate)
	{
		candidate = super.getEarlier(candidate);
		if (0 > compareMonthDayYear(getStartDate(), candidate))
		{
			candidate = getStartDate();
		}
		if (0 > compareMonthDayYear(getEndDate(), candidate))
		{
			candidate = getEndDate();
		}
		return candidate;
	}
	
	@Override
	public Calendar getLater(Calendar candidate)
	{
		candidate = super.getLater(candidate);
		if ( 0 < compareMonthDayYear(getStartDate(), candidate))
		{
			candidate = getStartDate();
		}
		if ( 0 < compareMonthDayYear(getEndDate(), candidate))
		{
			candidate = getEndDate();
		}
		return candidate;
	}
	
	public String buildRegisterReport(int iIndent)
	{
		return toString(iIndent);
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.buildLedgerReport(iIndent));
		sb.append(", Payee: ").append(getPayeeName());
		sb.append(", Amount: ").append(getAmount());
		sb.append(", Start Service: ").append(DateUtil.getTime(STANDARD_DATEFORMAT, getStartDate()));
		sb.append(", End Service: ").append(DateUtil.getTime(STANDARD_DATEFORMAT, getEndDate())).append("\n");
		return sb.toString();
	}

	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(super.toString(iIndent));
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Payee: ").append(getPayeeName()).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Amount: ").append(getAmount()).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Start Service: ").append(DateUtil.getTime(STANDARD_DATEFORMAT, getStartDate())).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("End Service: ").append(DateUtil.getTime(STANDARD_DATEFORMAT, getEndDate())).append("\n");
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
		if (null != getPayeeName())
		{
			iCode = HashUtil.hash(iCode, getPayeeName());
		}
		if (null != getAmount())
		{
			iCode = HashUtil.hash(iCode, getAmount());
		}
		if (null != getStartDate())
		{
			iCode = HashUtil.hash(iCode, getStartDate());
		}
		if (null != getEndDate())
		{
			iCode = HashUtil.hash(iCode, getEndDate());
		}
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