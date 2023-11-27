package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class PassiveTenants
{
	private List<PassiveTenant> m_lTenants;
	
	public PassiveTenants()
	{
	}
	
	public PassiveTenants(PassiveTenants source)
	{
		if (null != source)
		{
			List<PassiveTenant> lSource = source.getPassiveTenants();
			if (null != lSource)
			{
				for (PassiveTenant passiveTenantSource : lSource)
				{
					add(new PassiveTenant(passiveTenantSource));
				}
			}
		}
	}
	
	public void add(PassiveTenant entry)
	{
		if (null == m_lTenants)
		{
			m_lTenants = new ArrayList<PassiveTenant>();
		}
		m_lTenants.add(entry);
	}
	
	public void add(MoveOutEntry moveOut)
	{
		TenantEntry target = moveOut.getAssociatedTenantEntry();
		add(new PassiveTenant(target));
	}
	
	public boolean remove(RemoveEntry remove)
	{
		if (!isEmpty())
		{
			Object ooEntry = remove.getAssociatedTargetEntry();
			if (ooEntry instanceof TenantEntry)
			{
				TenantEntry target = (TenantEntry)ooEntry;
				for (PassiveTenant passiveTenant : m_lTenants)
				{
					if (passiveTenant.getTenant().equals(target))
					{
						return m_lTenants.remove(passiveTenant);
					}
				}
			}
		}
		return false;
	}
	
	public List<PassiveTenant> getPassiveTenants()
	{
		if (null != m_lTenants)
		{
			return Collections.unmodifiableList(m_lTenants);
		}
		return new ArrayList<PassiveTenant>();
	}
	
	public PassiveTenant getPassiveTenant(TenantEntry target)
	{
		if (null != m_lTenants)
		{
			for (PassiveTenant candidate : m_lTenants)
			{
				if (candidate.getTenant().equals(target))
				{
					return candidate;
				}
			}
		}
		return null;
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
			for (PassiveTenant entry : getPassiveTenants())
			{
				sb.append(entry.toString(iIndent)).append("\n");
			}
		}
		return sb.toString();
	}
}
