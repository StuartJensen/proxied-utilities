package com.pp.proxied.utilities.ledger;

import internal.atlaslite.jcce.convenience.Duet;

import java.util.Calendar;

public abstract class LedgerEntryVisitor
{
	private LedgerEntry m_previousLedgerEntry;
	private LedgerEntry m_currentLedgerEntry;
	private LedgerEntry m_nextLedgerEntry;
	
	private LedgerEntryVisitor m_previousVisitor;
	private LedgerEntryVisitor m_nextVisitor;
	
	public void setPreviousLedgerEntry(LedgerEntry previous)
	{
		m_previousLedgerEntry = previous;
	}
	
	public LedgerEntry getPreviousLedgerEntry()
	{
		return m_previousLedgerEntry;
	}
	
	public void setCurrentLedgerEntry(LedgerEntry current)
	{
		m_currentLedgerEntry = current;
	}
	
	public LedgerEntry getCurrentLedgerEntry()
	{
		return m_currentLedgerEntry;
	}
	
	public void setNextLedgerEntry(LedgerEntry next)
	{
		m_nextLedgerEntry = next;
	}
	
	public LedgerEntry getNextLedgerEntry()
	{
		return m_nextLedgerEntry;
	}
	
	public void setPreviousVisitor(LedgerEntryVisitor previous)
	{
		m_previousVisitor = previous;
	}
	
	public LedgerEntryVisitor getPreviousVisitor()
	{
		return m_previousVisitor;
	}
	
	public void setNextVisitor(LedgerEntryVisitor next)
	{
		m_nextVisitor = next;
	}
	
	public LedgerEntryVisitor getNextVisitor()
	{
		return m_nextVisitor;
	}
/*	
	public LedgerEntryVisitor getAtDate(Calendar targetDate)
	{
		LedgerEntryVisitor current = getHead();
		while (!current.getCurrentLedgerEntry().isOn(targetDate))
		{
			current = current.getNextVisitor();
		}
		return current;
	}
	*/
	
	public static Duet<LedgerEntryVisitor, LedgerEntryVisitor> getAtDate(LedgerEntryVisitor ledgerEntryVisitor, Calendar targetDate)
	{
		LedgerEntryVisitor current = ledgerEntryVisitor.getHead();
		while ((null != current) && (!current.getCurrentLedgerEntry().isOn(targetDate)))
		{
			current = current.getNextVisitor();
		}
		if (null != current)
		{	// Return before and after as same "Exact" entry
			return new Duet<LedgerEntryVisitor, LedgerEntryVisitor>(current, current);
		}
		// Exact date does not exist in ledger, find before and after entries
		LedgerEntryVisitor before = null;
		current = ledgerEntryVisitor.getHead();
		while ((null != current) && (current.getCurrentLedgerEntry().isBefore(targetDate)))
		{
			before = current;
			current = current.getNextVisitor();
		}
		// If before==null, after==non-null: head of list is AFTER, after == head
		// If before==non-null, after==null: tail of list is BEFORE, before == tail
		// If before==non-null, after==non-null: returned entries bound target date
		return new Duet<LedgerEntryVisitor, LedgerEntryVisitor>(before, current);
	}
	
	public boolean isOnOrBefore(Calendar that)
	{
		return getCurrentLedgerEntry().isOnOrBefore(that);
	}
	
	public boolean isBefore(Calendar that)
	{
		return getCurrentLedgerEntry().isBefore(that);
	}
	
	public boolean isOnOrAfter(Calendar that)
	{
		return getCurrentLedgerEntry().isOnOrAfter(that);
	}
	
	public boolean isOn(Calendar that)
	{
		return getCurrentLedgerEntry().isOn(that);
	}

	public LedgerEntryVisitor getHead()
	{
		LedgerEntryVisitor current = this;
		while (null != current.getPreviousVisitor())
		{
			current = current.getPreviousVisitor();
		}
		return current;
	}
	
	public LedgerEntryVisitor getTail()
	{
		LedgerEntryVisitor current = this;
		while (null != current.getNextVisitor())
		{
			current = current.getNextVisitor();
		}
		return current;
	}
	
	public abstract LedgerEntryVisitor getInstance();

	/**
	 * Only called on the root visitor instance
	 */
	public abstract void preProcess() throws InvalidLedgerException;
	
	/**
	 * Called on every visitor instance in a list
	 */
	public abstract void process() throws InvalidLedgerException;
	
	/**
	 * Only called on the root visitor instance
	 */
	public abstract void postProcess() throws InvalidLedgerException;
	
	public abstract String toString(int iIndent);
	
}
