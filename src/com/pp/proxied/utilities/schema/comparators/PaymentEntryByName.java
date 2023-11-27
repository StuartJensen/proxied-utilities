package com.pp.proxied.utilities.schema.comparators;

import java.util.Comparator;

import com.pp.proxied.utilities.register.schema.PaymentEntry;

public class PaymentEntryByName
	implements Comparator<PaymentEntry>
{
	// Sort PaymentEntry instances ONLY by name
	public int compare(PaymentEntry thisOne, PaymentEntry thatOne)
	{
		return thisOne.getPayeeName().compareTo(thatOne.getPayeeName());
	}
}
