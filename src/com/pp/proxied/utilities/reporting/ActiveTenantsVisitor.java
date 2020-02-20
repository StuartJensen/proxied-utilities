package com.pp.proxied.utilities.reporting;

import internal.atlaslite.jcce.convenience.Duet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.pp.proxied.utilities.ledger.InvalidLedgerException;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.LedgerEntryVisitor;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.MoveOutEntry;
import com.pp.proxied.utilities.schema.TenantEntry;

public class ActiveTenantsVisitor
	extends LedgerEntryVisitor
{
	private List<TenantEntry> m_lActiveTenants;
	
	public ActiveTenantsVisitor()
	{
		m_lActiveTenants = new ArrayList<TenantEntry>();
	}
	
	public List<TenantEntry> getActiveTenants()
	{
		return new ArrayList<TenantEntry>(m_lActiveTenants);
	}
	
	public int getActiveTenantCount()
	{
		return m_lActiveTenants.size();
	}
	
	public boolean isActive(TenantEntry tenantEntry)
	{
		if (0 != getActiveTenantCount())
		{
			return (m_lActiveTenants.contains(tenantEntry));
		}
		if (tenantEntry.equals(TenantEntry.getLandLordInstance()))
		{
			return true;
		}
		return false;
	}
	
	public static boolean isActiveOn(LedgerEntryVisitor ledgerEntryVisitor, TenantEntry tenantEntry, Calendar date)
		throws InvalidLedgerException
	{
		Duet<LedgerEntryVisitor, LedgerEntryVisitor> duetDates = LedgerEntryVisitor.getAtDate(ledgerEntryVisitor, date);
		if (duetDates.first == duetDates.second)
		{	// Exact date found
			return ((ActiveTenantsVisitor)duetDates.first).isActive(tenantEntry);
		}
		if ((null == duetDates.first) || (null == duetDates.second))
		{
			throw new InvalidLedgerException("Attempt to get date before of after ledger time period. Date: " + Entry.toString(date));
		}
		return ((ActiveTenantsVisitor)duetDates.first).isActive(tenantEntry);
	}
	
	@Override
	public LedgerEntryVisitor getInstance()
	{
		return new ActiveTenantsVisitor();
	}
	
	@Override
	public void preProcess()
		throws InvalidLedgerException
	{
	}
	
	@Override
	public void process()
		throws InvalidLedgerException
	{
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		
		if (null != getPreviousVisitor())
		{	// This is not the first entry. Pull tenants from the previous visitor.
			m_lActiveTenants = ((ActiveTenantsVisitor)getPreviousVisitor()).getActiveTenants();
		}
		
		// Process TENANT entries on the current ledger entry
		List<TenantEntry> lTenantEntries = currentLedgerEntry.getActiveTenants();
		if ((null != lTenantEntries) && (!lTenantEntries.isEmpty()))
		{
			for (TenantEntry tenantEntry : lTenantEntries)
			{
				if (!tenantEntry.isLandlordInstance())
				{	// Do not count the landlord as an active tenant.
					m_lActiveTenants.add(tenantEntry);
				}
			}
		}
		// Process MOVEOUT entries on the previous ledger entry. Process
		// MOVEOUT entries the day after they are entered in the ledger
		// so that the tenant moves out at midnight of the day where the
		// ledger entry is. Alternatly, TENANTs move in at midnight of
		// the day where the ledger entry resides.
		if (null != getPreviousLedgerEntry())
		{
			List<MoveOutEntry> lMoveOutEntries = getPreviousLedgerEntry().getActiveMoveOuts();
			if ((null != lMoveOutEntries) && (!lMoveOutEntries.isEmpty()))
			{
				for (MoveOutEntry moveOut : lMoveOutEntries)
				{
					String strTenantName = moveOut.getTenantName();
					for (TenantEntry tenantEntry : m_lActiveTenants)
					{
						if (tenantEntry.getTenantName().equals(strTenantName))
						{
							m_lActiveTenants.remove(tenantEntry);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void postProcess()
		throws InvalidLedgerException
	{
	}
	
	public void getAllTenantsActiveDuring(Calendar startDate, Calendar endDate, List<TenantEntry> lDestination)
	{
		Set<TenantEntry> setResult = new HashSet<TenantEntry>();
		Duet<LedgerEntryVisitor, LedgerEntryVisitor> duetDates = getAtDate(this, startDate);
		if (null != duetDates.first)
		{
			LedgerEntryVisitor currentActiveTenantVisitor = duetDates.first;
			while ((null != currentActiveTenantVisitor) &&
				   currentActiveTenantVisitor.isOnOrBefore(endDate))
			{
				List<TenantEntry> lActiveTenants = ((ActiveTenantsVisitor)currentActiveTenantVisitor).getActiveTenants();
				if (!lActiveTenants.isEmpty())
				{
					setResult.addAll(lActiveTenants);
				}
				else
				{
					setResult.add(TenantEntry.getLandLordInstance());
				}
				currentActiveTenantVisitor = currentActiveTenantVisitor.getNextVisitor();
			}
		}
		// Return results
		Iterator<TenantEntry> iter = setResult.iterator();
		while (iter.hasNext())
		{
			lDestination.add(iter.next());
		}
	}
	
	public Integer getPercentageActiveDuring(TenantEntry target, Calendar startDate, Calendar endDate)
		throws InvalidLedgerException
	{
		int iDayCount = 0;
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(startDate.getTime());
		while (0 >= Entry.compareMonthDayYear(currentDate, endDate))
		{
			if (ActiveTenantsVisitor.isActiveOn(this, target, currentDate))
			{
				iDayCount++;
			}
			Calendar nextDate = Calendar.getInstance();
			nextDate.setTime(currentDate.getTime());
			nextDate.add(Calendar.HOUR, 24);
			nextDate.set(Calendar.HOUR, 0);
			nextDate.set(Calendar.SECOND, 0);
			nextDate.set(Calendar.MILLISECOND, 0);
			currentDate = nextDate;
		}
		if (0 == iDayCount)
		{
			return 0;
		}
		int iDaysInPeriod = Entry.getDaysInPeriod(startDate, endDate);
		if (0 != iDaysInPeriod)
		{
			if (iDayCount > iDaysInPeriod)
			{
				iDayCount = iDaysInPeriod;
			}
			double dDaysInPeriod = iDaysInPeriod;
			double dDayCount = iDayCount;
			double dPercentage = dDayCount / dDaysInPeriod;
			return new Double(dPercentage * 100).intValue();
		}
		return 0;
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();		
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		sb.append(currentLedgerEntry.getDateString()).append(": ");
		sb.append("Active Tenants: ");
		for (TenantEntry tenantEntry : m_lActiveTenants)
		{
			sb.append("[").append(tenantEntry.getTenantName()).append("] ");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}

}
