package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;

public class RegisterFlushes
{
	private List<FlushEntry> m_lFlushes;
	
	public RegisterFlushes()
	{
	}
	
	public void add(FlushEntry entry)
	{
		if (null == m_lFlushes)
		{
			m_lFlushes = new ArrayList<FlushEntry>();
		}
		m_lFlushes.add(entry);
	}
	
	public List<FlushEntry> getFlushes()
	{
		return Collections.unmodifiableList(m_lFlushes);
	}
	
	public boolean isEmpty()
	{
		return (null == m_lFlushes) || (m_lFlushes.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (FlushEntry entry : getFlushes())
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
			for (FlushEntry entry : getFlushes())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}
}
