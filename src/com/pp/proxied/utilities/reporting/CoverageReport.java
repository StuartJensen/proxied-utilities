package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.PayeeEntry;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.RemoveEntry;
import com.pp.proxied.utilities.util.Coverage;
import com.pp.proxied.utilities.util.DateUtil;

import internal.atlaslite.jcce.convenience.Duet;
import internal.atlaslite.jcce.convenience.Trio;
import internal.atlaslite.jcce.util.StringUtil;

public class CoverageReport
{
	private Ledger m_ledger;
	private List<PayeeEntry> m_lAllPayees;

	public CoverageReport(Ledger ledger)
	{
		m_ledger = ledger;
		m_lAllPayees = new ArrayList<PayeeEntry>();
		List<LedgerEntry> ledgerEntries = m_ledger.getLedgerEntries();
		if ((null != ledgerEntries) && (!ledgerEntries.isEmpty()))
		{
			for (LedgerEntry entry : ledgerEntries)
			{
				List<PayeeEntry> lPayees = entry.getActivePayees();
				if ((null != lPayees) &&(!lPayees.isEmpty()))
				{
					m_lAllPayees.addAll(lPayees);
				}
			}
		}
	}
	
	public String toString(int iIndent)
	{
		Duet<Calendar, Calendar> ledgerDates = m_ledger.getBoundingDates();
		StringBuilder sb = new StringBuilder();
		
		// Gather Payee Service Coverage
		for (PayeeEntry payeeEntry : m_lAllPayees)
		{
			// Examine service coverage
			List<Duet<Calendar, Calendar>> lPeriods = new ArrayList<Duet<Calendar, Calendar>>();
			for (LedgerEntry entry : m_ledger.getLedgerEntries())
			{
				List<PaymentEntry> lPayments = entry.getActivePayments(payeeEntry);
				for (PaymentEntry payment : lPayments)
				{
					lPeriods.add(new Duet<Calendar, Calendar>(payment.getStartDate(), payment.getEndDate()));
				}
			}

			Calendar endDate = ledgerDates.second;
			RemoveEntry removePayeeEntry = Ledger.getRemovePayeeEntry(m_ledger.getEntries(), payeeEntry.getPayeeName());
			if (null != removePayeeEntry)
			{
				endDate = removePayeeEntry.getDate();
			}
			List<Duet<Calendar, Integer>> lCoverage = Coverage.getCoverage(payeeEntry.getDate(), endDate, lPeriods);
			String strCoverage = Coverage.toString(lCoverage, iIndent + 2);
			if (StringUtil.isDefined(strCoverage))
			{
				sb.append(StringUtil.getSpaces(iIndent + 1)).append("Service Coverage: ").append(payeeEntry.getPayeeName()).append("\n");
				sb.append(strCoverage);
			}
		}
		
		// Gather Active Tenant Coverage
		List<LedgerEntry> lLedgerEntries = m_ledger.getLedgerEntries();
		if (0 < lLedgerEntries.size())
		{
			LedgerEntry root = lLedgerEntries.get(0);
			List<Trio<Calendar, Calendar, Integer>> lActiveTenantPeriods = new ArrayList<Trio<Calendar, Calendar, Integer>>();
			ActiveTenantsVisitor currentVisitor = (ActiveTenantsVisitor)root.getVisitor(ActiveTenantsVisitor.class);
			ActiveTenantsVisitor startPeriodVisitor = currentVisitor;
			ActiveTenantsVisitor endPeriodVisitor = currentVisitor;
			int iTenantCount = currentVisitor.getActiveTenantCount();
			while (null != currentVisitor)
			{
				currentVisitor = (ActiveTenantsVisitor)currentVisitor.getNextVisitor();
				if ((null == currentVisitor) || (iTenantCount != currentVisitor.getActiveTenantCount()))
				{
					// To avoid time gaps between changes in active tenant counts:
					// - If the end date is the day before the current visitor date, then
					// all is well.
					// - If there is a gap between the end date and the current visitor date,
					// then the day before the current visitor date should be used.
					Calendar endDate = endPeriodVisitor.getCurrentLedgerEntry().getDate();
					if (null != currentVisitor)
					{
						if (!DateUtil.sequentialDays(endPeriodVisitor.getCurrentLedgerEntry().getDate(), currentVisitor.getCurrentLedgerEntry().getDate()))
						{
							endDate = DateUtil.getPreviousDay(currentVisitor.getCurrentLedgerEntry().getDate());
						}
					}
					lActiveTenantPeriods.add(new Trio<Calendar, Calendar, Integer>(startPeriodVisitor.getCurrentLedgerEntry().getDate(),
																				   endDate,
																				   endPeriodVisitor.getActiveTenantCount()));
					if (null != currentVisitor)
					{
						startPeriodVisitor = currentVisitor;
						iTenantCount = startPeriodVisitor.getActiveTenantCount();
					}
				}
				endPeriodVisitor = currentVisitor;
			}
			
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Active Tenants Coverage:\n");
			for (Trio<Calendar, Calendar, Integer> period : lActiveTenantPeriods)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append(Entry.toString(period.first)).append(" -- ").append(Entry.toString(period.second)).append(": Active Tenants: ").append(period.third.intValue()).append("\n");
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}

}
