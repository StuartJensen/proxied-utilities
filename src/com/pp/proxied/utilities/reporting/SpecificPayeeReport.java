package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.PayeeEntry;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.RemoveEntry;
import com.pp.proxied.utilities.util.Coverage;

import com.pp.proxied.utilities.util.GenericDouble;
import com.pp.proxied.utilities.util.StringUtil;

public class SpecificPayeeReport
{
	private Ledger m_ledger;
	private PayeeEntry m_payeeEntry;
	
	public SpecificPayeeReport(Ledger ledger, PayeeEntry payeeEntry)
	{
		m_ledger = ledger;
		m_payeeEntry = payeeEntry;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		List<LedgerEntry> ledgerEntries = m_ledger.getLedgerEntries();
		if ((null == ledgerEntries) || (ledgerEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		else if ((null == m_payeeEntry) || (!StringUtil.isDefined(m_payeeEntry.getPayeeName())))
		{
			sb.append("No payee specified.");
		}
		else
		{
			sb.append(StringUtil.getIndent(iIndent)).append("Payee Report: ").append(m_payeeEntry.getPayeeName()).append("\n");
			for (LedgerEntry entry : ledgerEntries)
			{
				sb.append(entry.toString(m_payeeEntry, iIndent + 1));
			}
			
			// Examine service coverage
			List<GenericDouble<Calendar, Calendar>> lPeriods = new ArrayList<GenericDouble<Calendar, Calendar>>();
			for (LedgerEntry entry : ledgerEntries)
			{
				List<PaymentEntry> lPayments = entry.getActivePayments(m_payeeEntry);
				for (PaymentEntry payment : lPayments)
				{
					lPeriods.add(new GenericDouble<Calendar, Calendar>(payment.getStartDate(), payment.getEndDate()));
				}
			}
			
			// Examine coverage from Payee Activation date to the end of the ledger OR the date the payee was removed.
			GenericDouble<Calendar, Calendar> ledgerDates = m_ledger.getBoundingDates();
			Calendar endDate = ledgerDates.second;
			RemoveEntry removePayeeEntry = Ledger.getRemovePayeeEntry(m_ledger.getEntries(), m_payeeEntry.getPayeeName());
			if (null != removePayeeEntry)
			{
				endDate = removePayeeEntry.getDate();
			}
			List<GenericDouble<Calendar, Integer>> lCoverage = Coverage.getCoverage(m_payeeEntry.getDate(), endDate, lPeriods);
			String strCoverage = Coverage.toString(lCoverage, iIndent + 2);
			if (StringUtil.isDefined(strCoverage))
			{
				sb.append(StringUtil.getIndent(iIndent + 1)).append("Service Coverage:\n");
				sb.append(strCoverage);
			}
		}
		return sb.toString();
	}

}
