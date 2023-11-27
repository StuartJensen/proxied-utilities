package com.pp.proxied.utilities.extra.paid;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.StringUtil;

public class ExtraPaidManager
{
	public static ExtraPaidManager m_instance;
	
	private Map<TenantEntry, Boolean> m_mapExtraPaid;
	private Map<TenantEntry, AtomicInteger> m_mapAsCandidateCount;
	private Map<TenantEntry, AtomicInteger> m_mapSelectedCount;
	
	private ExtraPaidManager()
	{
		m_mapExtraPaid = new HashMap<TenantEntry, Boolean>();
		m_mapAsCandidateCount = new HashMap<TenantEntry, AtomicInteger>();
		m_mapSelectedCount = new HashMap<TenantEntry, AtomicInteger>();
	}

	public static ExtraPaidManager getInstance()
	{
		if (null == m_instance)
		{
			m_instance = new ExtraPaidManager();
		}
		return m_instance;
	}
	
	public void add(TenantEntry tenantEntry)
	{
		m_mapExtraPaid.put(tenantEntry, Boolean.FALSE);
	}
	
	public Boolean get(TenantEntry tenantEntry)
	{
		return m_mapExtraPaid.get(tenantEntry);
	}
	
	public void set(TenantEntry tenantEntry, Boolean b)
	{
		m_mapExtraPaid.put(tenantEntry, b);
	}
	
	public TenantEntry getNotExtraPaid(List<TenantEntry> lTenants)
	{
		incAsCandidateCount(lTenants);
		while (true)
		{
			for (TenantEntry tenantEntry : lTenants)
			{
				Boolean b = get(tenantEntry);
				if (b == Boolean.FALSE)
				{
					set(tenantEntry, Boolean.TRUE);
					incSelectedCount(tenantEntry);
					return tenantEntry;
				}
			}
			for (TenantEntry tenantEntry : lTenants)
			{
				set(tenantEntry, Boolean.FALSE);
			}
		}
	}
	
	public Set<TenantEntry> getTenants()
	{
		return m_mapExtraPaid.keySet();
	}
	
	public TenantEntry getTenant(String strTenantName)
	{
		Set<TenantEntry> setCandidates = getTenants();
		for (TenantEntry candidate: setCandidates)
		{
			if (strTenantName.equals(candidate.getTenantName()))
			{
				return candidate;
			}
		}
		return null;
	}
	
	private void incAsCandidateCount(List<TenantEntry> lTenantEntries)
	{
		if (null != lTenantEntries)
		{
			for (TenantEntry candidate : lTenantEntries)
			{
				AtomicInteger iCount = m_mapAsCandidateCount.get(candidate);
				if (null == iCount)
				{
					iCount = new AtomicInteger(0);
					m_mapAsCandidateCount.put(candidate, iCount);
				}
				iCount.incrementAndGet();
			}
		}
	}
	
	private int incSelectedCount(TenantEntry tenantEntry)
	{
		AtomicInteger iCount = m_mapSelectedCount.get(tenantEntry);
		if (null == iCount)
		{
			iCount = new AtomicInteger(0);
			m_mapSelectedCount.put(tenantEntry, iCount);
		}
		return iCount.incrementAndGet();
	}
	
	public String buildExtraPaidReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		Iterator<TenantEntry> iter = m_mapSelectedCount.keySet().iterator();
		while (iter.hasNext())
		{
			TenantEntry key = iter.next();
			AtomicInteger value = m_mapSelectedCount.get(key);
			AtomicInteger valueAsCandidate = m_mapAsCandidateCount.get(key);
			if ((null != value) && (null != valueAsCandidate))
			{
				double dSelectedCount = (double)value.get();
				double dAsCandidateCount = (double)valueAsCandidate.get();
				double dSelectionPercentage = dSelectedCount / dAsCandidateCount;
				dSelectionPercentage *= 100;
				sb.append(StringUtil.getSpaces(iIndent)).append(key.getTenantName()).append(": Extra Paid: ").append(value.get()).append(", Was Candidate: ").append(valueAsCandidate).append(", Was Selected: ").append(new DecimalFormat("##.####").format(dSelectionPercentage)).append("%\n");
			}
		}
		return sb.toString();
	}
}
