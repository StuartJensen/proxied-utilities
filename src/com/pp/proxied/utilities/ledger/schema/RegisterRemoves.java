package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class RegisterRemoves
{
	private List<RemoveEntry> m_lRemoves;
	
	public RegisterRemoves()
	{
	}
	
	public void add(RemoveEntry entry)
	{
		if (null == m_lRemoves)
		{
			m_lRemoves = new ArrayList<RemoveEntry>();
		}
		m_lRemoves.add(entry);
	}
	
	public List<RemoveEntry> getRemoves()
	{
		return Collections.unmodifiableList(m_lRemoves);
	}
	
	public RemoveEntry getRemove(TenantEntry target)
	{
		if (null != m_lRemoves)
		{
			for (RemoveEntry remove : m_lRemoves)
			{
				if (remove.getAssociatedTargetEntry() instanceof TenantEntry)
				{
					TenantEntry tenantEntry = (TenantEntry)remove.getAssociatedTargetEntry();
					if (tenantEntry.getTenantName().equals(target.getTenantName()))
					{
						return remove;
					}
				}
			}
		}
		return null;
	}
	
	public boolean isEmpty()
	{
		return (null == m_lRemoves) || (m_lRemoves.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (RemoveEntry entry : getRemoves())
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
			for (RemoveEntry entry : getRemoves())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}
}
