package com.pp.proxied.utilities.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;

public class DateUtil
{
	public static String toString(Calendar date)
	{
		return getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, date.getTimeInMillis());
	}
	
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
		if (previousDay.get(Calendar.YEAR) == nextDay.get(Calendar.YEAR))
		{
			return ((iNext - 1) == iPrevious);
		}
		
		if ((previousDay.get(Calendar.YEAR) + 1) != nextDay.get(Calendar.YEAR))
		{	// Non-sequential years
			return false;
		}
		// Make sure previous is December 31 and next is January 1
		return ((iPrevious == previousDay.getMaximum(Calendar.DAY_OF_YEAR)) &&
				(iNext == nextDay.getMinimum(Calendar.DAY_OF_YEAR)));
	}
	
	public static Calendar getPreviousDay(Calendar day)
	{
		Calendar previousDay = Calendar.getInstance();
		previousDay.setTimeInMillis(day.getTimeInMillis());
		previousDay.add(Calendar.DAY_OF_YEAR, -1);
		return previousDay;
	}
	
	public static Calendar getNextDay(Calendar day)
	{
		Calendar nextDay = Calendar.getInstance();
		nextDay.setTimeInMillis(day.getTimeInMillis());
		nextDay.add(Calendar.DAY_OF_YEAR, 1);
		return nextDay;
	}
}
