package com.pp.proxied.utilities.ledger;

import java.util.ArrayList;
import java.util.List;

import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenant;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayments;
import com.pp.proxied.utilities.ledger.schema.ActiveTenants;
import com.pp.proxied.utilities.ledger.schema.RegisterPayments;
import com.pp.proxied.utilities.register.schema.PaymentEntry;

public class ActivePaymentProcessor
{
	public void process(Ledger ledger)
	{	// Gather all the register payment entries into an ordered list. Then
		// process each one. This must be done to avoid a concurrent modification
		// exception being thrown when setting the active payments in the ledger
		// entry.
		List<RegisterPayments> lToBeProcessed = new ArrayList<RegisterPayments>();
		for (LedgerEntry current : ledger.getLedgerEntries())
		{
			RegisterPayments processingPayments = current.getRegisterPayments();
			if ((null != processingPayments) && (!processingPayments.isEmpty()))
			{
				lToBeProcessed.add(processingPayments);
			}
		}
		// Now process each of the gathered register payment entries
		for (RegisterPayments current : lToBeProcessed)
		{
			for (PaymentEntry processingPayment : current.getPayments())
			{
				ActivePayment previous = null;
				for (LedgerEntry candidate : ledger.getLedgerEntries())
				{
					if (processingPayment.inServiceOn(candidate.getDate()))
					{
						ActivePayment activePayment = new ActivePayment(candidate.getDate(), processingPayment);
						activePayment.setPrevious(previous);
						previous = activePayment;
						candidate.addActivePayment(activePayment);
						// If there are active tenants on this ledger entry, then add
						// active tenant payment entries to the active payment.
						ActiveTenants activeTenants = candidate.getActiveTenants();
						if (null != activeTenants)
						{
							ActiveTenantPayments activeTenantsPayments = new ActiveTenantPayments();
							activePayment.setActiveTenantPayments(activeTenantsPayments);
							for (ActiveTenant activeTenant : activeTenants.getActiveTenants())
							{
								activeTenantsPayments.add(new ActiveTenantPayment(activeTenant, activePayment));
							}
						}
					}
				}
			}
		}
	}
}
