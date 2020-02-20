package com.pp.proxied.utilities.util;

import java.util.Calendar;

public class DateUtil
{
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
