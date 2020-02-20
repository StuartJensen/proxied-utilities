package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pp.proxied.utilities.ledger.InvalidLedgerException;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.LedgerEntryVisitor;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.TenantEntry;
import com.pp.proxied.utilities.util.DateUtil;

public class PaymentPercentageVisitor
	extends LedgerEntryVisitor
{
	private Map<PaymentEntry, Map<TenantEntry, Integer>> m_mapPerPaymentTenantPercentges;
	
	public PaymentPercentageVisitor()
	{
		m_mapPerPaymentTenantPercentges = new HashMap<PaymentEntry, Map<TenantEntry, Integer>>();
	}
	
	@Override
	public LedgerEntryVisitor getInstance()
	{
		return new PaymentPercentageVisitor();
	}
	
	public Map<TenantEntry, Integer> getPercentagesForPayment(PaymentEntry paymentEntry)
	{
		return m_mapPerPaymentTenantPercentges.get(paymentEntry);
	}
	
	public Integer getPercentageForPayment(PaymentEntry paymentEntry, TenantEntry tenantEntry)
	{
		Map<TenantEntry, Integer> mapTenant = m_mapPerPaymentTenantPercentges.get(paymentEntry);
		if (null != mapTenant)
		{
			return mapTenant.get(tenantEntry);
		}
		return null;
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
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		ActiveTenantsVisitor activeTenantVisitor = (ActiveTenantsVisitor)currentLedgerEntry.getVisitor(ActiveTenantsVisitor.class);
		if (null != activeTenantVisitor)
		{
			List<PaymentEntry> lPayments = currentLedgerEntry.getActivePayments();
			if ((null != lPayments) && (!lPayments.isEmpty()))
			{
				for (PaymentEntry paymentEntry : lPayments)
				{
					Map<TenantEntry, Integer> mapTenant = new HashMap<TenantEntry, Integer>();
					m_mapPerPaymentTenantPercentges.put(paymentEntry, mapTenant);
					// Populate mapTenant
					List<TenantEntry> lActiveTenantsForPayment = new ArrayList<TenantEntry>();
					activeTenantVisitor.getAllTenantsActiveDuring(paymentEntry.getStartDate(), paymentEntry.getEndDate(), lActiveTenantsForPayment);
					for (TenantEntry tenantEntry : lActiveTenantsForPayment)
					{
						mapTenant.put(tenantEntry, activeTenantVisitor.getPercentageActiveDuring(tenantEntry, paymentEntry.getStartDate(), paymentEntry.getEndDate()));
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
		
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		sb.append(DateUtil.getTime(Entry.STANDARD_DATEFORMAT, currentLedgerEntry.getDate().getTimeInMillis())).append("\n");
		if ((null != m_mapPerPaymentTenantPercentges) && (!m_mapPerPaymentTenantPercentges.isEmpty()))
		{
			Iterator<PaymentEntry> iterPayment = m_mapPerPaymentTenantPercentges.keySet().iterator();
			while (iterPayment.hasNext())
			{
				PaymentEntry payementEntry = iterPayment.next();
				sb.append("Payee: ").append(payementEntry.getPayeeName()).append("\n");
				Map<TenantEntry, Integer> mapTenants = m_mapPerPaymentTenantPercentges.get(payementEntry);
				Iterator<TenantEntry> iterTenant = mapTenants.keySet().iterator();
				while (iterTenant.hasNext())
				{
					TenantEntry tenantEntry = iterTenant.next();
					Integer iPercentActive = mapTenants.get(tenantEntry);
					sb.append(tenantEntry.getTenantName()).append(": ").append(iPercentActive).append("%\n");
				}
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

