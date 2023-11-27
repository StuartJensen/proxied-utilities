package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class ActiveTenants
{
	private List<ActiveTenant> m_lTenants;
	
	public ActiveTenants()
	{
	}
	
	public ActiveTenants(ActiveTenants source)
	{
		if (null != source)
		{
			List<ActiveTenant> lSource = source.getActiveTenants();
			if (null != lSource)
			{
				for (ActiveTenant activeTenantSource : lSource)
				{
					add(new ActiveTenant(activeTenantSource));
				}
			}
		}
	}
	
	public void add(ActiveTenant entry)
	{
		if (null == m_lTenants)
		{
			m_lTenants = new ArrayList<ActiveTenant>();
		}
		m_lTenants.add(entry);
	}
	
	public boolean remove(MoveOutEntry moveOut)
	{
		if (!isEmpty())
		{
			TenantEntry target = moveOut.getAssociatedTenantEntry();
			for (ActiveTenant activeTenant : m_lTenants)
			{
				if (activeTenant.getTenant().equals(target))
				{
					return m_lTenants.remove(activeTenant);
				}
			}
		}
		return false;
	}
	
	public List<ActiveTenant> getActiveTenants()
	{
		if (null != m_lTenants)
		{
			return Collections.unmodifiableList(m_lTenants);
		}
		return new ArrayList<ActiveTenant>();
	}
	
	public ActiveTenant getActiveTenant(TenantEntry target)
	{
		if (null != m_lTenants)
		{
			for (ActiveTenant candidate : m_lTenants)
			{
				if (candidate.getTenant().equals(target))
				{
					return candidate;
				}
			}
		}
		return null;
	}
	
	public int size()
	{
		if (null != m_lTenants)
		{
			return m_lTenants.size();
		}
		return 0;
	}
	
	public boolean isEmpty()
	{
		return (null == m_lTenants) || (m_lTenants.isEmpty());
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (ActiveTenant entry : getActiveTenants())
			{
				sb.append(entry.toString(iIndent)).append("\n");
			}
		}
		return sb.toString();
	}

}
