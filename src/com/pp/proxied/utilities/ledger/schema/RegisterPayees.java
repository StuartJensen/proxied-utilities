package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;

public class RegisterPayees
{
	private List<PayeeEntry> m_lPayees;
	
	public RegisterPayees()
	{
	}
	
	public void add(PayeeEntry entry)
	{
		if (null == m_lPayees)
		{
			m_lPayees = new ArrayList<PayeeEntry>();
		}
		m_lPayees.add(entry);
	}
	
	public List<PayeeEntry> getPayees()
	{
		return Collections.unmodifiableList(m_lPayees);
	}
	
	public boolean isEmpty()
	{
		return (null == m_lPayees) || (m_lPayees.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (PayeeEntry entry : getPayees())
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
			for (PayeeEntry entry : getPayees())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}

}
