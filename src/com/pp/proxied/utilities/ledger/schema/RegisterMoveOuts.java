package com.pp.proxied.utilities.ledger.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;

public class RegisterMoveOuts
{
	private List<MoveOutEntry> m_lMoveOuts;
	
	public RegisterMoveOuts()
	{
	}
	
	public void add(MoveOutEntry entry)
	{
		if (null == m_lMoveOuts)
		{
			m_lMoveOuts = new ArrayList<MoveOutEntry>();
		}
		m_lMoveOuts.add(entry);
	}
	
	public List<MoveOutEntry> getMoveOuts()
	{
		return Collections.unmodifiableList(m_lMoveOuts);
	}
	
	public boolean isEmpty()
	{
		return (null == m_lMoveOuts) || (m_lMoveOuts.isEmpty());
	}
	
	public String buildLedgerReport(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (!isEmpty())
		{
			for (MoveOutEntry entry : getMoveOuts())
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
			for (MoveOutEntry entry : getMoveOuts())
			{
				sb.append(entry.toString(iIndent));
			}
		}
		return sb.toString();
	}

}
