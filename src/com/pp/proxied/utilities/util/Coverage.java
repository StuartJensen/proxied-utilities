package com.pp.proxied.utilities.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.schema.Entry;

public class Coverage
{
	public static List<GenericDouble<Calendar, Integer>> getCoverage(Calendar startDate, Calendar endDate, List<GenericDouble<Calendar, Calendar>> lCoveragePeriods)
	{
		List<GenericDouble<Calendar, Integer>> lResult = new ArrayList<GenericDouble<Calendar, Integer>>();
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(startDate.getTime());
		while (0 >= Entry.compareMonthDayYear(currentDate, endDate))
		{
			int iCoveredCount = 0;
			for (GenericDouble<Calendar, Calendar> coverage : lCoveragePeriods)
			{
				if (isIn(currentDate, coverage.first, coverage.second))
				{
					iCoveredCount++;
				}
			}
			lResult.add(new GenericDouble<Calendar, Integer>(currentDate, iCoveredCount));

			Calendar nextDate = Calendar.getInstance();
			nextDate.setTime(currentDate.getTime());
			nextDate.add(Calendar.HOUR, 24);
			nextDate.set(Calendar.HOUR, 0);
			nextDate.set(Calendar.SECOND, 0);
			nextDate.set(Calendar.MILLISECOND, 0);
			currentDate = nextDate;
		}
		return lResult;
	}
		
	public static boolean isIn(Calendar target, Calendar start, Calendar end)
	{
		return ((0 <= Entry.compareMonthDayYear(target, start)) && (0 >= Entry.compareMonthDayYear(target, end)));
	}
	
	public static String toString(List<GenericDouble<Calendar, Integer>> lCoverage, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != lCoverage) && (!lCoverage.isEmpty()))
		{
			int iCurrentCoverage = -1;
			Calendar start = null;
			Calendar end = null;;
			for (GenericDouble<Calendar, Integer> coverage : lCoverage)
			{
				if (null == start)
				{
					start = coverage.first;
					iCurrentCoverage = coverage.second.intValue();
				}
				else
				{
					if (iCurrentCoverage != coverage.second.intValue())
					{
						sb.append(StringUtil.getIndent(iIndent)).append(Entry.toString(start)).append(" - ").append(Entry.toString(end)).append(": Coverage: ").append(iCurrentCoverage).append("\n");
						start = coverage.first;
						iCurrentCoverage = coverage.second.intValue();
					}
				}
				end = coverage.first;
			}
			sb.append(StringUtil.getIndent(iIndent)).append(Entry.toString(start)).append(" - ").append(Entry.toString(end)).append(": Coverage: ").append(iCurrentCoverage).append("\n");
		}
		return sb.toString();
	}


}
