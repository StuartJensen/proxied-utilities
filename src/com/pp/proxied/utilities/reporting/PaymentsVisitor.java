package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pp.proxied.utilities.ledger.InvalidLedgerException;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.LedgerEntryVisitor;
import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.TenantEntry;
import com.pp.proxied.utilities.util.PaymentDetails;
import com.pp.proxied.utilities.util.Percentages;

public class PaymentsVisitor
	extends LedgerEntryVisitor
{
	private Map<PaymentEntry, List<PaymentDetails>> m_mapPayments;
	private Map<TenantEntry, MoneyInteger> m_mapExtraPaidBeforeProcessing;
	private Map<TenantEntry, MoneyInteger> m_mapExtraPaidDuringProcessing;
	
	public PaymentsVisitor()
	{
		m_mapPayments = new HashMap<PaymentEntry, List<PaymentDetails>>();
	}
	
	public Map<PaymentEntry, List<PaymentDetails>> getPayments()
	{
		if (null != m_mapPayments)
		{
			return Collections.unmodifiableMap(m_mapPayments);
		}
		return Collections.emptyMap();
	}
	
	public List<PaymentDetails> getPaymentDetails(PaymentEntry paymentEntry)
	{
		return m_mapPayments.get(paymentEntry);
	}
	
	public PaymentDetails getPaymentDetails(PaymentEntry paymentEntry, TenantEntry tenantEntry)
	{
		List<PaymentDetails> lDetails = m_mapPayments.get(paymentEntry);
		if (null != lDetails)
		{
			for (PaymentDetails details : lDetails)
			{
				if (tenantEntry.getTenantName().equals(details.getTenantEntry().getTenantName()))
				{
					return details;
				}
			}
		}
		return null;
	}
	
	public int getPaymentPayeeCount(PaymentEntry paymentEntry)
	{
		List<PaymentDetails> lDetails = m_mapPayments.get(paymentEntry);
		if (null != lDetails)
		{
			return lDetails.size();
		}
		return 0;
	}
	
	@Override
	public LedgerEntryVisitor getInstance()
	{
		return new PaymentsVisitor();
	}
	
	public Map<TenantEntry, MoneyInteger> getExtraPaidBeforeProcessing()
	{
		return Collections.unmodifiableMap(m_mapExtraPaidBeforeProcessing);
	}

	public Map<TenantEntry, MoneyInteger> getExtraPaidDuringProcessing()
	{
		if (null != m_mapExtraPaidDuringProcessing)
		{
			return Collections.unmodifiableMap(m_mapExtraPaidDuringProcessing);
		}
		return Collections.emptyMap();
	}
	
	@Override
	public void preProcess()
		throws InvalidLedgerException
	{

	}
	
	@Override
	public void process()
		throws InvalidLedgerException
	{
		PaymentsVisitor previous = (PaymentsVisitor)getPreviousVisitor();
		if (null == previous)
		{
			m_mapExtraPaidBeforeProcessing = new HashMap<TenantEntry, MoneyInteger>();
		}
		else
		{
			Map<TenantEntry, MoneyInteger> previouslyExtraPaidBeforeProcessing = previous.getExtraPaidBeforeProcessing();
			Map<TenantEntry, MoneyInteger> previouslyExtraPaidDuringProcessing = previous.getExtraPaidDuringProcessing();
			m_mapExtraPaidBeforeProcessing = new HashMap<TenantEntry, MoneyInteger>();
			Iterator<TenantEntry> iter = previouslyExtraPaidBeforeProcessing.keySet().iterator();
			while (iter.hasNext())
			{
				TenantEntry tenantEntry = iter.next();
				MoneyInteger tenantsExtraPaidSoFar = previouslyExtraPaidBeforeProcessing.get(tenantEntry);
				MoneyInteger previouslyExtraPaidDuringProcessingAmount = previouslyExtraPaidDuringProcessing.get(tenantEntry);
				if (null != previouslyExtraPaidDuringProcessingAmount)
				{
					tenantsExtraPaidSoFar = tenantsExtraPaidSoFar.plus(previouslyExtraPaidDuringProcessingAmount);
				}
				m_mapExtraPaidBeforeProcessing.put(tenantEntry, tenantsExtraPaidSoFar);
			}
		}
		
		// Get all active tenants so we can make sure the extra paid before processing
		// list is complete with all tenants.
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		ActiveTenantsVisitor activeTenantsVisitor = (ActiveTenantsVisitor)currentLedgerEntry.getVisitor(ActiveTenantsVisitor.class);
		List<TenantEntry> lActiveTenants = activeTenantsVisitor.getActiveTenants();
		
		// Add all unknown (so far) tenants
		for (TenantEntry tenantEntry : lActiveTenants)
		{
			if (null == m_mapExtraPaidBeforeProcessing.get(tenantEntry))
			{
				m_mapExtraPaidBeforeProcessing.put(tenantEntry, MoneyInteger.ZERO);
			}
		}
		
		List<PaymentEntry> lPayments = currentLedgerEntry.getActivePayments();
		if (null != lPayments)
		{
			PaymentPercentageVisitor paymentPercentageVisitor = (PaymentPercentageVisitor)currentLedgerEntry.getVisitor(PaymentPercentageVisitor.class);
			for (PaymentEntry paymentEntry : lPayments)
			{
				List<PaymentDetails> lPaymentDetails = new ArrayList<PaymentDetails>();
				m_mapPayments.put(paymentEntry, lPaymentDetails);
				
				Map<TenantEntry, Integer> mapTenants = paymentPercentageVisitor.getPercentagesForPayment(paymentEntry);
				if ((null != mapTenants) && !mapTenants.isEmpty())
				{
					//List<Integer> lPercentages = new ArrayList<Integer>();
					Iterator<TenantEntry> iterTenants = mapTenants.keySet().iterator();
					while (iterTenants.hasNext())
					{
						TenantEntry tenantEntry = iterTenants.next();
						lPaymentDetails.add(new PaymentDetails(tenantEntry,
															   mapTenants.get(tenantEntry), new MoneyInteger(mapTenants.get(tenantEntry)),
															   m_mapExtraPaidBeforeProcessing.get(tenantEntry)));
					}
					Percentages.adjustWeightedSumToTarget(paymentEntry.getAmount().getAmount(), lPaymentDetails);
				}
			}
		}
		
		// Gather all extra paid for each tenant
		Collection<List<PaymentDetails>> lAllDetails = m_mapPayments.values();
		if (null != lAllDetails)
		{
			for (List<PaymentDetails> lDetails : lAllDetails)
			{
				for (PaymentDetails details: lDetails)
				{
					MoneyInteger extraPaidDuringProcessing = details.getExtraPaidDuringProcessing();
					if (0 != extraPaidDuringProcessing.getAmount())
					{
						if (null == m_mapExtraPaidDuringProcessing)
						{
							m_mapExtraPaidDuringProcessing = new HashMap<TenantEntry, MoneyInteger>();
						}
						MoneyInteger existingForTenant = m_mapExtraPaidDuringProcessing.get(details.getTenantEntry());
						if (null == existingForTenant)
						{
							m_mapExtraPaidDuringProcessing.put(details.getTenantEntry(), extraPaidDuringProcessing);
						}
						else
						{
							m_mapExtraPaidDuringProcessing.remove(details.getTenantEntry());
							extraPaidDuringProcessing = extraPaidDuringProcessing.plus(existingForTenant);
							m_mapExtraPaidDuringProcessing.put(details.getTenantEntry(), extraPaidDuringProcessing);
						}
					}
				}
			}
		}
	}
		
	@Override
	public void postProcess()
		throws InvalidLedgerException
	{

	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if ((null != m_mapPayments) && (!m_mapPayments.isEmpty()))
		{
			LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
			sb.append(currentLedgerEntry.getDateString()).append(": ");
			sb.append("Payment: ");
			Iterator<PaymentEntry> iterPayments = m_mapPayments.keySet().iterator();
			while (iterPayments.hasNext())
			{
				PaymentEntry paymentEntry = iterPayments.next();
				sb.append("Payee: ").append(paymentEntry.getPayeeName());
				sb.append(" Amount: ").append(paymentEntry.getAmount()).append("\n");
				List<PaymentDetails> lDetails = m_mapPayments.get(paymentEntry);
				if (null != lDetails)
				{
					for(PaymentDetails details : lDetails)
					{
						sb.append("[").append(details.getTenantEntry().getTenantName()).append(": ").append(details.getPercentage()).append("%: $").append(details.getAmountDisplayName()).append("]\n");
					}
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
	

}