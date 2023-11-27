package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class RegisterDeposits
{
	private List<DepositEntry> m_lDeposits;
	
	public RegisterDeposits()
	{
	}
	
	public void add(DepositEntry entry)
	{
		if (null == m_lDeposits)
		{
			m_lDeposits = new ArrayList<DepositEntry>();
		}
		m_lDeposits.add(entry);
	}
	
	public List<DepositEntry> getDeposits()
	{
		return Collections.unmodifiableList(m_lDeposits);
	}
	
	public DepositEntry getDeposit(TenantEntry target)
	{
		if (null != m_lDeposits)
		{
			for (DepositEntry deposit : m_lDeposits)
			{
				if (target.equals(deposit.getAssociatedTenantEntry()))
				{
					return deposit;
				}
			}
		}
		return null;
	}
	
	public boolean isEmpty()
	{
		return (null == m_lDeposits) || (m_lDeposits.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (DepositEntry entry : getDeposits())
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
			for (DepositEntry entry : getDeposits())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}
}
