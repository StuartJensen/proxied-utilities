package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.extra.paid.ExtraPaidManager;
import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class ActiveTenantPayments
{
	private List<ActiveTenantPayment> m_lTenantPayments;
	
	public ActiveTenantPayments()
	{
	}
	
	public ActiveTenantPayments(ActiveTenantPayments source)
	{
		if (null != source)
		{
			List<ActiveTenantPayment> lSource = source.getActiveTenantPayments();
			if (null != lSource)
			{
				for (ActiveTenantPayment activePaymentSource : lSource)
				{
					add(new ActiveTenantPayment(activePaymentSource));
				}
			}
		}
	}
	
	public void updateFromPrevious(ActiveTenantPayments previous)
	{
		for (ActiveTenantPayment source : previous.getActiveTenantPayments())
		{
			ActiveTenantPayment destination = find(source);
			if (null != destination)
			{
				destination.setTotalPaidAmount(source.getTotalPaidAmount());
			}
		}
	}
	
	private ActiveTenantPayment find(ActiveTenantPayment source)
	{
		for (ActiveTenantPayment candidate : getActiveTenantPayments())
		{
			if (candidate.getActiveTenant().getTenant().equals(source.getActiveTenant().getTenant()))
			{
				return candidate;
			}
		}
		return null;
	}
	
	public void add(ActiveTenantPayment entry)
	{
		if (null == m_lTenantPayments)
		{
			m_lTenantPayments = new ArrayList<ActiveTenantPayment>();
		}
		m_lTenantPayments.add(entry);
	}
	
	public List<ActiveTenantPayment> getActiveTenantPayments()
	{
		return Collections.unmodifiableList(m_lTenantPayments);
	}
	
	public void spendExtraCents(MoneyInteger iCents)
	{
		while (!MoneyInteger.ZERO.equals(iCents))
		{
			ActiveTenantPayment entry = getNotExtraPaid();
			entry.incPaidAmount();
			iCents = iCents.minus(1);
		}
	}
	
	private ActiveTenantPayment getNotExtraPaid()
	{
		if ((null != m_lTenantPayments) && (!m_lTenantPayments.isEmpty()))
		{
			List<TenantEntry> lCandidates = new ArrayList<TenantEntry>();
			for (ActiveTenantPayment current : m_lTenantPayments)
			{
				lCandidates.add(current.getActiveTenant().getTenant());
			}
			TenantEntry selected = ExtraPaidManager.getInstance().getNotExtraPaid(lCandidates);
			for (ActiveTenantPayment current : m_lTenantPayments)
			{
				if (selected.equals(current.getActiveTenant().getTenant()))
				{
					return current;
				}
			}
		}
		throw new IllegalStateException("ERROR: Zero Tenants. Unable to find phantom tenant in tenants list.");
	}
	
	public boolean isEmpty()
	{
		return (null == m_lTenantPayments) || (m_lTenantPayments.isEmpty());
	}
	
	public int size()
	{
		if (null != m_lTenantPayments)
		{
			return m_lTenantPayments.size();
		}
		return 0;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (ActiveTenantPayment entry : getActiveTenantPayments())
			{
				sb.append(entry.toString(iIndent)).append("\n");
			}
		}
		return sb.toString();
	}

}
