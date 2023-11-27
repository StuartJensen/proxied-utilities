package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.schema.RegisterPayees;
import com.pp.proxied.utilities.ledger.schema.RegisterPayments;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.util.Coverage;
import com.pp.proxied.utilities.util.GenericDouble;
import com.pp.proxied.utilities.util.StringUtil;
import com.pp.proxied.utilities.util.GenericTriple;

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
				RegisterPayees registerPayees = entry.getRegisterPayees();
				if ((null != registerPayees) && (!registerPayees.isEmpty()))
				{
					List<PayeeEntry> lPayees = registerPayees.getPayees();
					m_lAllPayees.addAll(lPayees);
				}
			}
		}
	}
	
	public String toString(int iIndent)
	{
		GenericDouble<Calendar, Calendar> ledgerDates = m_ledger.getBoundingDates();
		StringBuilder sb = new StringBuilder();
		
		// Gather Payee Service Coverage
		for (PayeeEntry payeeEntry : m_lAllPayees)
		{
			// Examine service coverage
			List<GenericDouble<Calendar, Calendar>> lPeriods = new ArrayList<GenericDouble<Calendar, Calendar>>();
			List<LedgerEntry> ledgerEntries = m_ledger.getLedgerEntries();
			if ((null != ledgerEntries) && (!ledgerEntries.isEmpty()))
			{
				for (LedgerEntry entry : ledgerEntries)
				{
					RegisterPayments registerPayments = entry.getRegisterPayments();
					if ((null != registerPayments) && (!registerPayments.isEmpty()))
					{
						List<PaymentEntry> lPayments = registerPayments.getPayments(payeeEntry);
						for (PaymentEntry payment : lPayments)
						{
							lPeriods.add(new GenericDouble<Calendar, Calendar>(payment.getStartDate(), payment.getEndDate()));
						}
					}
				}
			}

			Calendar endDate = ledgerDates.second;
			RemoveEntry removePayeeEntry = m_ledger.getRemovePayeeEntry(payeeEntry);
			if (null != removePayeeEntry)
			{
				endDate = removePayeeEntry.getDate();
			}
			List<GenericDouble<Calendar, Integer>> lCoverage = Coverage.getCoverage(payeeEntry.getDate(), endDate, lPeriods);
			String strCoverage = Coverage.toString(lCoverage, iIndent + 2);
			if (StringUtil.isDefined(strCoverage))
			{
				sb.append(StringUtil.getSpaces(iIndent + 1)).append("Service Coverage: ").append(payeeEntry.getPayeeName()).append("\n");
				sb.append(strCoverage);
			}
		}
		
		// Gather Active Tenant Coverage
		List<LedgerEntry> ledgerEntries = m_ledger.getLedgerEntries();
		if ((null != ledgerEntries) && (!ledgerEntries.isEmpty()))
		{
			List<GenericTriple<Calendar, Calendar, Integer>> lPeriods = new ArrayList<GenericTriple<Calendar, Calendar, Integer>>();
			Calendar startPeriod = null;
			Calendar endPeriod = null;
			Integer tenantCount = null; 
			for (LedgerEntry entry : ledgerEntries)
			{
				int iTenantCount = 0;
				if (null != entry.getActiveTenants())
				{
					iTenantCount = entry.getActiveTenants().size();
				}
				if ((null == startPeriod) && (null == endPeriod) && (null == tenantCount))
				{	// First entry
					startPeriod = entry.getDate();
					endPeriod = entry.getDate();
					tenantCount = new Integer(entry.getActiveTenants().size());
				}
				else if (iTenantCount == tenantCount.intValue())
				{
					endPeriod = entry.getDate();
				}
				else if (iTenantCount != tenantCount.intValue())
				{
					lPeriods.add(new GenericTriple<Calendar, Calendar, Integer>(startPeriod, endPeriod, tenantCount));
					tenantCount = Integer.valueOf(iTenantCount);
					startPeriod = entry.getDate();
					endPeriod = entry.getDate();
				}
			}
			sb.append(StringUtil.getSpaces(iIndent + 1)).append("Active Tenants Coverage:\n");
			for (GenericTriple<Calendar, Calendar, Integer> period : lPeriods)
			{
				sb.append(StringUtil.getSpaces(iIndent + 2)).append(RegisterBaseEntry.toString(period.first)).append(" - ").append(RegisterBaseEntry.toString(period.second)).append(": Active Tenants: ").append(period.third.intValue()).append("\n");
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
