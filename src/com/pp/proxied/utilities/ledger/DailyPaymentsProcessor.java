package com.pp.proxied.utilities.ledger;

import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActivePayments;

public class DailyPaymentsProcessor
{
	public void process(Ledger ledger)
	{
		for (LedgerEntry current : ledger.getLedgerEntries())
		{
			ActivePayments activePayments = current.getActivePayments();
			if ((null != activePayments) && (!activePayments.isEmpty()))
			{
				for (ActivePayment activePayment : activePayments.getActivePayments())
				{
					activePayment.calculate();
				}
			}
		}
	}
}
