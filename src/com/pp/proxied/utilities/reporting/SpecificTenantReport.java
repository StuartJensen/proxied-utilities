package com.pp.proxied.utilities.reporting;

import internal.atlaslite.jcce.util.StringUtil;

import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.TenantEntry;

public class SpecificTenantReport
{
	private List<LedgerEntry> m_ledgerEntries;
	private TenantEntry m_tenantEntry;
	
	public SpecificTenantReport(Ledger ledger, TenantEntry tenantEntry)
	{
		m_ledgerEntries = ledger.getLedgerEntries();
		m_tenantEntry = tenantEntry;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if ((null == m_ledgerEntries) || (m_ledgerEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		else if ((null == m_tenantEntry) || (!StringUtil.isDefined(m_tenantEntry.getTenantName())))
		{
			sb.append("No tenant specified.");
		}
		else
		{
			sb.append("Tenant Report: ").append(m_tenantEntry.getTenantName()).append("\n");
			for (LedgerEntry entry : m_ledgerEntries)
			{
				sb.append(entry.toString(m_tenantEntry, 0));
			}
		}
		return sb.toString();
	}
}
