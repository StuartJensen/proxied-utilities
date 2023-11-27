package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayments;
import com.pp.proxied.utilities.register.schema.PaymentEntry;

public class PaymentsReport
{
	private Ledger m_ledger;
	
	public PaymentsReport(Ledger ledger)
	{
		m_ledger = ledger;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		List<LedgerEntry>lEntries = m_ledger.getLedgerEntries();
		if ((null == lEntries) || (lEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		else
		{	// Gather all payment registry entries
			List<PaymentEntry> sAllPayments = new ArrayList<PaymentEntry>();
			for (LedgerEntry entry : lEntries)
			{
				if ((null != entry.getRegisterPayments()) && (!entry.getRegisterPayments().isEmpty()))
				{
					sAllPayments.addAll(entry.getRegisterPayments().getPayments());
				}
			}

			// Process each payment
			for (PaymentEntry paymentEntry : sAllPayments)
			{
				PaymentDetails details = new PaymentDetails(paymentEntry);
				for (LedgerEntry entry : lEntries)
				{
					ActivePayment activePayment = entry.getActivePayment(paymentEntry);
					if (null != activePayment)
					{
						ActiveTenantPayments atps = activePayment.getActiveTenantPayments();
						if ((null != atps) && (!atps.isEmpty()))
						{
							for (ActiveTenantPayment atp : atps.getActiveTenantPayments())
							{
								details.incDayCount(atp.getActiveTenant().getTenant());
								details.setPaidAmount(atp.getActiveTenant().getTenant(), atp.getTotalPaidAmount());
							}
						}
					}
				}
				sb.append(details.report());	
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
}
