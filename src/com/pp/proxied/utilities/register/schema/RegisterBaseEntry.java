package com.pp.proxied.utilities.register.schema;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.HashUtil;
import com.pp.proxied.utilities.util.ObjectUtil;
import com.pp.proxied.utilities.util.StringUtil;

public abstract class RegisterBaseEntry
	implements Comparable<RegisterBaseEntry>
{
	public static SimpleDateFormat STANDARD_DATEFORMAT = new SimpleDateFormat("MM/dd/yyyy");
	public static final int INVALID_NTH = -1; 
	
	private int m_iNthEntry = INVALID_NTH;
	private Calendar m_date;
	private Verb m_verb;
	
	private RegisterBaseEntry m_previousEntry;
	private RegisterBaseEntry m_nextEntry;
	
	public RegisterBaseEntry(Calendar date, Verb verb)
	{
		setDate(date);
		setVerb(verb);
	}
	
	public RegisterBaseEntry(String strDate, Verb verb)
		throws ParseException
	{
		setDate(strDate);
		setVerb(verb);
	}
	
	public void setNth(int iNthEntry)
	{
		m_iNthEntry = iNthEntry;
	}
	
	public int getNth()
	{
		return m_iNthEntry;
	}
	
	public void setDate(Calendar date)
	{
		m_date = date;
	}

	public void setDate(String strDate)
		throws ParseException
	{
		m_date = parseDate(strDate);
	}
	
	public Calendar getDate()
	{
		return m_date;
	}
	
	public String getDateString()
	{
		return DateUtil.getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, getDate().getTimeInMillis());
	}
	
	public static String getDateString(Calendar date)
	{
		return DateUtil.getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, date.getTimeInMillis());
	}
	
	public void setPreviousEntry(RegisterBaseEntry previousEntry)
	{
		m_previousEntry = previousEntry;
	}
	
	public RegisterBaseEntry getPreviousEntry()
	{
		return m_previousEntry;
	}
	
	public void setNextEntry(RegisterBaseEntry nextEntry)
	{
		m_nextEntry = nextEntry;
	}
	
	public RegisterBaseEntry getNextEntry()
	{
		return m_nextEntry;
	}
	
   /**
    * Is {@code that} date on or before this entry's date?
    * 
    * @param that The date to test.
    * @return {@code true} iff {@code that} date is on or before this entry's date
    */
	public boolean isOnOrBefore(Calendar that)
	{
		return (0 >= compareMonthDayYear(getDate(), that));
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
	
   /**
    * Is {@code that} date after this entry's date?
    * 
    * @param that The date to test.
    * @return {@code true} iff {@code that} date is after this entry's date
    */
	public boolean isAfter(Calendar that)
	{
		return (0 < compareMonthDayYear(getDate(), that));
	}
	
   /**
    * Is {@code that} date before this entry's date?
    * 
    * @param that The date to test.
    * @return {@code true} iff {@code that} date is before this entry's date
    */
	public boolean isBefore(Calendar that)
	{
		return (0 > compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOn(Calendar that)
	{
		return (0 == compareMonthDayYear(getDate(), that));
	}
	
	public static boolean isSameMonthDayYear(Calendar one, Calendar two)
	{
		return (0 == compareMonthDayYear(one, two));
	}
	
	/**
	 * Compare two dates year, month and day only.
	 * 
	 * @param one First date. Must not be {@code null}
	 * @param two Second date. Must not be {@code null}
	 * @return 0: equal, negative: one before two.
	 * positive: two before one
	 */
	public static int compareMonthDayYear(Calendar one, Calendar two)
	{
		int iOneYear = one.get(Calendar.YEAR);
		int iTwoYear = two.get(Calendar.YEAR);
		int iDiff = iOneYear - iTwoYear;
		if (0 != iDiff)
		{
			return iDiff;
		}
		
		int iOneMonth = one.get(Calendar.MONTH);
		int iTwoMonth = two.get(Calendar.MONTH);
		iDiff = iOneMonth - iTwoMonth;
		if (0 != iDiff)
		{
			return iDiff;
		}
		
		int iOneDayOfMonth = one.get(Calendar.DAY_OF_MONTH);
		int iTwoDayOfMonth = two.get(Calendar.DAY_OF_MONTH);
		return (iOneDayOfMonth - iTwoDayOfMonth);
	}
	
	public int compareTo(RegisterBaseEntry that)
	{
		int iResult = getDate().compareTo(that.getDate());
		if (iResult == 0)
		{	// Must compare verb type sort order
			iResult = (getVerb().getSortOrder() - that.getVerb().getSortOrder());
		}
		return iResult;
	}
	
	public static int getDaysInPeriod(Calendar startDate, Calendar endDate)
	{
		if (endDate.get(Calendar.YEAR) == startDate.get(Calendar.YEAR))
		{
			return (endDate.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR));
		}
		
		Calendar endOfStartYear = Calendar.getInstance();
		endOfStartYear.set(startDate.get(Calendar.YEAR), Calendar.DECEMBER, 31, 0, 0);
		int iDaysStartYear = (endOfStartYear.get(Calendar.DAY_OF_YEAR) - startDate.get(Calendar.DAY_OF_YEAR));
		return iDaysStartYear + (endDate.get(Calendar.DAY_OF_YEAR));
	}
	
	public static int getDaysInPeriodInclusive(Calendar startDate, Calendar endDate)
	{
		return getDaysInPeriod(startDate, endDate) + 1;
	}
	
	public static Calendar parseDate(String strDate)
		throws ParseException
	{
		Calendar date = Calendar.getInstance();
		date.setTime(STANDARD_DATEFORMAT.parse(strDate));
		return date;
	}
	
	public Calendar getEarlier(Calendar candidate)
	{
		if (null == candidate)
		{
			return getDate();
		}
		if ( 0 > compareMonthDayYear(getDate(), candidate))
		{
			return getDate();
		}
		return candidate;
	}
	
	public Calendar getLater(Calendar candidate)
	{
		if (null == candidate)
		{
			return getDate();
		}
		if ( 0 < compareMonthDayYear(getDate(), candidate))
		{
			return getDate();
		}
		return candidate;
	}
	
	public static String toString(Calendar date)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(date.get(Calendar.MONTH) + 1).append("/");
		sb.append(date.get(Calendar.DAY_OF_MONTH)).append("/");
		sb.append(date.get(Calendar.YEAR));
		return sb.toString();
	}
	
	public void setVerb(Verb verb)
	{
		m_verb = verb;
	}
	
	public Verb getVerb()
	{
		return m_verb;
	}
	
	@Override
	public int hashCode()
	{
		int iCode = 1801;
		if (null != m_date)
		{
			iCode = HashUtil.hash(iCode, m_date);
		}
		if (null != m_verb)
		{
			iCode = HashUtil.hash(iCode, m_verb);
		}
		return iCode;
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (that instanceof RegisterBaseEntry)
		{
			if (this == that)
			{	// Same instance
				return true;
			}
			if ((ObjectUtil.areReferencesEqual(this.getDate(), ((RegisterBaseEntry)that).getDate())) &&
				(this.getVerb() != ((RegisterBaseEntry)that).getVerb()))
			{
				return true;
			}
		}
		return false;
	}
	
	public String buildRegisterReport(int iIndent)
	{
		return toString();
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder(StringUtil.getSpaces(iIndent)).append("Register: ").append(getClass().getSimpleName());
		return sb.toString();
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder(StringUtil.getSpaces(iIndent) + getClass().getSimpleName() + "\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Date: ").append(DateUtil.getTime(STANDARD_DATEFORMAT, getDate())).append("\n");
		sb.append(StringUtil.getSpaces(iIndent + 1)).append("Verb: ").append(getVerb()).append("\n");
		return sb.toString();
	}
}
