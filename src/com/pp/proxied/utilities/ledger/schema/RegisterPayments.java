package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;

public class RegisterPayments
{
	private List<PaymentEntry> m_lPayments;
	
	public RegisterPayments()
	{
	}
	
	public void add(PaymentEntry entry)
	{
		if (null == m_lPayments)
		{
			m_lPayments = new ArrayList<PaymentEntry>();
		}
		m_lPayments.add(entry);
	}
	
	public List<PaymentEntry> getPayments()
	{
		return Collections.unmodifiableList(m_lPayments);
	}
	
	public List<PaymentEntry> getPayments(PayeeEntry target)
	{
		List<PaymentEntry> lResults = new ArrayList<PaymentEntry>();
		if (null != m_lPayments)
		{
			for (PaymentEntry candidate : m_lPayments)
			{
				if (candidate.getPayeeName().equals(target.getPayeeName()))
				{
					lResults.add(candidate);
				}
			}
		}
		return lResults;
	}
	
	public boolean isEmpty()
	{
		return (null == m_lPayments) || (m_lPayments.isEmpty());
	}
	
	public int size()
	{
		if (null != m_lPayments)
		{
			return m_lPayments.size();
		}
		return 0;
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (PaymentEntry entry : getPayments())
			{
				sb.append(entry.buildLedgerReport(iIndent));
			}
		}
		return sb.toString();
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (PaymentEntry entry : getPayments())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}
}
