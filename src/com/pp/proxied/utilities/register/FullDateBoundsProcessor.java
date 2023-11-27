package com.pp.proxied.utilities.register;

import java.util.Calendar;

import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.GenericDouble;

public class FullDateBoundsProcessor
{
	public GenericDouble<Calendar, Calendar> process(Register register)
	{
		GenericDouble<Calendar, Calendar> bounding = register.getBoundingDates();
		Calendar start = bounding.first;
		Calendar end = bounding.second;
		for (RegisterBaseEntry entry : register.getEntries())
		{
			if (entry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)entry;
				if (RegisterEntry.isBefore(paymentEntry.getStartDate(), start))
				{
					start = paymentEntry.getStartDate();
				}
				if (RegisterEntry.isAfter(paymentEntry.getEndDate(), end))
				{
					end = paymentEntry.getEndDate();
				}
			}
			else if (entry instanceof MoveOutEntry)
			{	// If there is a MOVEOUT entry on the last date, make sure
				// an additional day is added at the end.
				if ((RegisterEntry.isOn(entry.getDate(), bounding.second)) &&
					(RegisterEntry.isOnOrAfter(entry.getDate(), end)))
				{
					end = DateUtil.getNextDay(entry.getDate());
					System.out.println("Expanded ledger end date to " + RegisterBaseEntry.getDateString(end) + " due to MOVEOUT");
				}
			}
		}
		return new GenericDouble<Calendar, Calendar>(start, end);
	}
}
