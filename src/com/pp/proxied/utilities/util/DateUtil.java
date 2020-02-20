package com.pp.proxied.utilities.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil
{
	public static String getTime(DateFormat df, long lTimeInMillis)
	{
		return df.format(new Date(lTimeInMillis));
	}
	
	public static String getTime(DateFormat df, Calendar calendar)
	{
		return df.format(calendar.getTime());
	}
	
	public static boolean sequentialDays(Calendar previousDay, Calendar nextDay)
	{
		int iPrevious = previousDay.get(Calendar.DAY_OF_YEAR);
		int iNext = nextDay.get(Calendar.DAY_OF_YEAR);
		return ((iNext - 1) == iPrevious);
	}
	
	public static Calendar getPreviousDay(Calendar day)
	{
		long lDayMillis = day.getTimeInMillis();
		long lPrevoiusDayInMillis = lDayMillis - (24 * 60 * 60 *1000);
		Calendar previousDay = Calendar.getInstance();
		previousDay.setTimeInMillis(lPrevoiusDayInMillis);
		return previousDay;
	}
}
