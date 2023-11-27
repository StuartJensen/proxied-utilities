package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class RegisterTenants
{
	private List<TenantEntry> m_lTenants;
	
	public RegisterTenants()
	{
	}
	
	public void add(TenantEntry entry)
	{
		if (null == m_lTenants)
		{
			m_lTenants = new ArrayList<TenantEntry>();
		}
		m_lTenants.add(entry);
	}
	
	public List<TenantEntry> getTenants()
	{
		return Collections.unmodifiableList(m_lTenants);
	}
	
	public boolean isEmpty()
	{
		return (null == m_lTenants) || (m_lTenants.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (TenantEntry entry : getTenants())
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
			for (TenantEntry entry : getTenants())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}
}
