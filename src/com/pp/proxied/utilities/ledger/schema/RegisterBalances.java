package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class RegisterBalances
{
	private List<BalanceEntry> m_lBalances;
	
	public RegisterBalances()
	{
	}
	
	public void add(BalanceEntry entry)
	{
		if (null == m_lBalances)
		{
			m_lBalances = new ArrayList<BalanceEntry>();
		}
		m_lBalances.add(entry);
	}
	
	public List<BalanceEntry> getBalances()
	{
		return Collections.unmodifiableList(m_lBalances);
	}
	
	public BalanceEntry getRegisterBalance(TenantEntry target)
	{
		if ((null != m_lBalances) && (!m_lBalances.isEmpty()))
		{
			for(BalanceEntry candidate : m_lBalances)
			{
				if (candidate.getTenantName().equals(target.getTenantName()))
				{
					return candidate;
				}
			}
		}
		return null;
	}
	
	public boolean isEmpty()
	{
		return (null == m_lBalances) || (m_lBalances.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (BalanceEntry entry : getBalances())
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
			for (BalanceEntry entry : getBalances())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}

}
