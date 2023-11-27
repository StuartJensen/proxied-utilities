package com.pp.proxied.utilities.schema.comparators;

import java.util.Calendar;
import java.util.Comparator;

import com.pp.proxied.utilities.register.schema.PaymentEntry;

public class PaymentEntryByStartDate
	implements Comparator<PaymentEntry>
{
	// Sort PaymentEntry instances ONLY by start date
	public int compare(PaymentEntry thisOne, PaymentEntry thatOne)
	{
		Calendar startOne = thisOne.getStartDate();
		Calendar startTwo = thatOne.getStartDate();
		return startOne.compareTo(startTwo);
	}
}
