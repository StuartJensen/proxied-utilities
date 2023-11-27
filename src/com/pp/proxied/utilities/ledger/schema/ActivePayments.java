package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class ActivePayments
{
	private List<ActivePayment> m_lPayments;
	
	public ActivePayments()
	{
	}
	
	public ActivePayments(ActivePayments source)
	{
		if (null != source)
		{
			List<ActivePayment> lSource = source.getActivePayments();
			if (null != lSource)
			{
				for (ActivePayment activePaymentSource : lSource)
				{
					add(new ActivePayment(activePaymentSource));
				}
			}
		}
	}
	
	public void add(ActivePayment entry)
	{
		if (null == m_lPayments)
		{
			m_lPayments = new ArrayList<ActivePayment>();
		}
		m_lPayments.add(entry);
	}
	
	public List<ActivePayment> getActivePayments()
	{
		return Collections.unmodifiableList(m_lPayments);
	}
	
	public List<ActiveTenantPayment> getActiveTenantPayments(TenantEntry target)
	{
		List<ActiveTenantPayment> lResult = new ArrayList<ActiveTenantPayment>();
		if (null != m_lPayments)
		{
			for (ActivePayment activePayment : m_lPayments)
			{
				ActiveTenantPayment payment = activePayment.getActiveTenantPayment(target);
				if (null != payment)
				{
					lResult.add(payment);
				}
			}
		}
		return lResult;
	}
	
	public List<ActiveTenantPayment> getActiveTenantPayments(PayeeEntry target)
	{
		List<ActiveTenantPayment> lResult = new ArrayList<ActiveTenantPayment>();
		if (null != m_lPayments)
		{
			for (ActivePayment activePayment : m_lPayments)
			{
				lResult.addAll(activePayment.getActiveTenantPayments(target));
			}
		}
		return lResult;
	}
	
	public boolean isEmpty()
	{
		return (null == m_lPayments) || (m_lPayments.isEmpty());
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (ActivePayment entry : getActivePayments())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}

}
