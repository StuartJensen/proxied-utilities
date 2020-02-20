package com.pp.proxied.utilities.ledger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.pp.proxied.utilities.schema.BalanceEntry;
import com.pp.proxied.utilities.schema.DepositEntry;
import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.FlushEntry;
import com.pp.proxied.utilities.schema.MoveOutEntry;
import com.pp.proxied.utilities.schema.PayeeEntry;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.RemoveEntry;
import com.pp.proxied.utilities.schema.TenantEntry;
import com.pp.proxied.utilities.schema.Verb;

import com.pp.proxied.utilities.util.GenericDouble;
import com.pp.proxied.utilities.util.StringUtil;

public class Ledger
{
	private List<Entry> m_lEntries;
	private List<LedgerEntry> m_lLedgerEntries;
	
	public Ledger(List<Entry> lEntries)
	{
		m_lLedgerEntries = new ArrayList<LedgerEntry>();
		m_lEntries = lEntries;
		
		int iNthLedgerEntry = 0;
		LedgerEntry currentLedgerEntry = null;
		for (Entry entry : lEntries)
		{
			if ((null == currentLedgerEntry) ||
				(0 != Entry.compareMonthDayYear(entry.getDate(), currentLedgerEntry.getDate())))
			{
				currentLedgerEntry = new LedgerEntry(entry.getDate(), iNthLedgerEntry++);
				m_lLedgerEntries.add(currentLedgerEntry);
			}
			if (entry instanceof DepositEntry)
			{
				currentLedgerEntry.addDeposit((DepositEntry)entry);
			}
			else if (entry instanceof BalanceEntry)
			{
				currentLedgerEntry.addBalance((BalanceEntry)entry);
			}
			else if (entry instanceof TenantEntry)
			{
				currentLedgerEntry.addTenant((TenantEntry)entry);
			}
			else if (entry instanceof PayeeEntry)
			{
				currentLedgerEntry.addPayee((PayeeEntry)entry);
			}
			else if (entry instanceof MoveOutEntry)
			{
				currentLedgerEntry.addMoveOut((MoveOutEntry)entry);
			}
			else if (entry instanceof PaymentEntry)
			{
				currentLedgerEntry.addPayment((PaymentEntry)entry);
			}
			else if (entry instanceof RemoveEntry)
			{
				currentLedgerEntry.addRemove((RemoveEntry)entry);
			}
			else if (entry instanceof FlushEntry)
			{
				currentLedgerEntry.addFlush((FlushEntry)entry);
			}
		}
		//Duet<Calendar, Calendar> dateDomain = getDateDomain(lEntries);
		//expandLedger(dateDomain.first, dateDomain.second, m_lEntries);
		//populateLedger(m_lEntries, lEntries);
	}
	
	public List<LedgerEntry> getLedgerEntries()
	{
		return m_lLedgerEntries;
	}
	
	public List<Entry> getEntries()
	{
		return m_lEntries;
	}
	
	public GenericDouble<Calendar, Calendar> getBoundingDates()
	{
		return new GenericDouble<Calendar, Calendar>(m_lEntries.get(0).getDate(), m_lEntries.get(m_lEntries.size() - 1).getDate());
	}
	
	public void attach(LedgerEntryVisitor root, boolean bProcess)
		throws InvalidLedgerException
	{
		LedgerEntryVisitor currentVisitor = root;
		List<LedgerEntry> lEntries = getLedgerEntries();
		for (int i=0; i<lEntries.size(); i++)
		{
			currentVisitor.setCurrentLedgerEntry(lEntries.get(i));
			lEntries.get(i).add(currentVisitor);
			if (i > 0)
			{
				currentVisitor.setPreviousLedgerEntry(lEntries.get(i - 1));
			}
			if (i < (lEntries.size() - 1))
			{
				root.setNextLedgerEntry(lEntries.get(i + 1));
				LedgerEntryVisitor nextVisitor = currentVisitor.getInstance();
				currentVisitor.setNextVisitor(nextVisitor);
				nextVisitor.setPreviousVisitor(currentVisitor);
				currentVisitor = nextVisitor;
			}
		}
		if (bProcess)
		{
			currentVisitor = root;
			while (null != currentVisitor)
			{
				currentVisitor.preProcess();
				currentVisitor = currentVisitor.getNextVisitor();
			}
			
			currentVisitor = root;
			while (null != currentVisitor)
			{
				currentVisitor.process();
				currentVisitor = currentVisitor.getNextVisitor();
			}
			
			currentVisitor = root;
			while (null != currentVisitor)
			{
				currentVisitor.postProcess();
				currentVisitor = currentVisitor.getNextVisitor();
			}
		}
	}
	
	public String toString(LedgerEntryVisitor root, int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		if (null != root)
		{
			LedgerEntryVisitor currentVisitor = root;
			while (null != currentVisitor)
			{
				sb.append(currentVisitor.toString(iIndent));
				currentVisitor = currentVisitor.getNextVisitor();
			}
		}
		return sb.toString();
	}
	
	public static void validate(List<Entry> lEntries)
		throws InvalidLedgerException
	{
		// Enforce non-empty AND unique tenant and payee names
		Collection<String> cTenantNames = new ArrayList<String>();
		Collection<String> cPayeeNames = new ArrayList<String>();
		for (Entry entry : lEntries)
		{
			if (entry instanceof TenantEntry)
			{
				TenantEntry tenantEntry = (TenantEntry)entry;
				String strTenantName = tenantEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidLedgerException("TENANT entry exists with empty tenant name. Date: " + tenantEntry.getDateString());
				}
				if (cTenantNames.contains(strTenantName))
				{
					throw new InvalidLedgerException("Duplicate TENANT entry. Date: " + tenantEntry.getDateString());
				}
				cTenantNames.add(strTenantName);
			}
			else if (entry instanceof PayeeEntry)
			{
				PayeeEntry payeeEntry = (PayeeEntry)entry;
				String strPayeeName = payeeEntry.getPayeeName();
				if (!StringUtil.isDefined(strPayeeName))
				{
					throw new InvalidLedgerException("PAYEE entry exists with empty payee name. Date: " + payeeEntry.getDateString());
				}
				if (cPayeeNames.contains(strPayeeName))
				{
					throw new InvalidLedgerException("Duplicate PAYEE entry. Date: " + payeeEntry.getDateString());
				}
				cPayeeNames.add(strPayeeName);
			}
		}
		
		// Validate all tenant and payee names in BALANCE, PAYMENT, DEPOSIT, MOVEOUT, REMOVE, FLUSH are resolvable
		List<RemoveEntry> lRemovedPayees = new ArrayList<RemoveEntry>();
		for (Entry entry : lEntries)
		{
			if (entry instanceof BalanceEntry)
			{
				BalanceEntry balanceEntry = (BalanceEntry)entry;
				String strTenantName = balanceEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidLedgerException("BALANCE entry exists with empty tenant name. Date: " + balanceEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTenantName);
				if (null == associatedTenant)
				{
					throw new InvalidLedgerException("Unresolvable tenant name \"" + strTenantName + "\" in BALANCE entry. Date: " + balanceEntry.getDateString());
				}
				if (balanceEntry.getNth() < associatedTenant.getNth())
				{
					throw new InvalidLedgerException("BALANCE entry (" + balanceEntry.getNth() + ") positioned before TENANT entry (" + associatedTenant.getNth() + ") in ledger.");
				}
				balanceEntry.setAssociatedTenantEntry(associatedTenant);
			}
			else if (entry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)entry;
				String strPayeeName = paymentEntry.getPayeeName();
				if (!StringUtil.isDefined(strPayeeName))
				{
					throw new InvalidLedgerException("PAYMENT entry exists with empty payee name. Date: " + paymentEntry.getDateString());
				}
				if (0 < Entry.compareMonthDayYear(paymentEntry.getStartDate(), paymentEntry.getEndDate()))
				{
					throw new InvalidLedgerException("PAYMENT entry exists with end date AFTER start date. Date: " + paymentEntry.getDateString());
				}
				PayeeEntry associatedPayee = getPayeeEntry(lEntries, strPayeeName);
				if (null == associatedPayee)
				{
					throw new InvalidLedgerException("Unresolvable payee name \"" + strPayeeName + "\" in PAYMENT entry. Date: " + paymentEntry.getDateString());
				}
				if (paymentEntry.getNth() < associatedPayee.getNth())
				{
					throw new InvalidLedgerException("PAYMENT entry (" + paymentEntry.getNth() + ") positioned before PAYEE entry (" + associatedPayee.getNth() + ") in ledger.");
				}
				if (0 > Entry.compareMonthDayYear(paymentEntry.getStartDate(), associatedPayee.getDate()))
				{
					throw new InvalidLedgerException("PAYMENT entry (" + paymentEntry.getDateString() + ") with service start date (" + Entry.toString(paymentEntry.getStartDate()) + ") before PAYEE entry (" + associatedPayee.getDateString() + ") in ledger.");
				}
				paymentEntry.setAssociatedPayeeEntry(associatedPayee);
				// Ensure this payment service time period falls within an "active" payee
				RemoveEntry removedEntry = getRemoveEntry(lRemovedPayees, associatedPayee.getPayeeName());
				if (null != removedEntry)
				{	// Yes, this payee was removed
					if (removedEntry.isAfter(paymentEntry.getEndDate()))
					{
						throw new InvalidLedgerException("PAYMENT entry (" + paymentEntry.getDateString() + ") has a service end date that extends after a Payee (" + associatedPayee.getPayeeName() + ") was removed on (" + removedEntry.getDateString() + ")\n");
					}
				}
			}
			else if (entry instanceof DepositEntry)
			{
				DepositEntry depositEntry = (DepositEntry)entry;
				String strTenantName = depositEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidLedgerException("DEPOSIT entry exists with empty tenant name. Date: " + depositEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTenantName);
				if (null == associatedTenant)
				{
					throw new InvalidLedgerException("Unresolvable tenant name \"" + strTenantName + "\" in DEPOSIT entry. Date: " + depositEntry.getDateString());
				}
				if (depositEntry.getNth() < associatedTenant.getNth())
				{
					throw new InvalidLedgerException("DEPOSIT entry (" + depositEntry.getNth() + ") positioned before TENANT entry (" + associatedTenant.getNth() + ") in ledger.");
				}
				depositEntry.setAssociatedTenantEntry(associatedTenant);
			}
			else if (entry instanceof MoveOutEntry)
			{
				MoveOutEntry moveOutEntry = (MoveOutEntry)entry;
				String strTenantName = moveOutEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidLedgerException("MOVEOUT entry exists with empty tenant name. Date: " + moveOutEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTenantName);
				if (null == associatedTenant)
				{
					throw new InvalidLedgerException("Unresolvable tenant name \"" + strTenantName + "\" in MOVEOUT entry. Date: " + moveOutEntry.getDateString());
				}
				if (moveOutEntry.getNth() < associatedTenant.getNth())
				{
					throw new InvalidLedgerException("MOVEOUT entry (" + moveOutEntry.getNth() + ") positioned after TENANT entry (" + associatedTenant.getNth() + ") in ledger.");
				}
				moveOutEntry.setAssociatedTenantEntry(associatedTenant);
			}
			else if (entry instanceof RemoveEntry)
			{
				RemoveEntry removeEntry = (RemoveEntry)entry;
				String strTargetName = removeEntry.getTargetName();
				if (!StringUtil.isDefined(strTargetName))
				{
					throw new InvalidLedgerException("REMOVE entry exists with empty target name. Date: " + removeEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTargetName);
				PayeeEntry associatedPayee = getPayeeEntry(lEntries, strTargetName);
				if ((null == associatedTenant) && (null == associatedPayee))
				{
					throw new InvalidLedgerException("Unresolvable target name \"" + strTargetName + "\" in REMOVE entry. No matching tenant nor payee. Date: " + removeEntry.getDateString());
				}
				if ((null != associatedTenant) && (null != associatedPayee))
				{
					throw new InvalidLedgerException("Multiple resolutions for target name \"" + strTargetName + "\" in REMOVE entry. Both tenant and payee match. Date: " + removeEntry.getDateString());
				}
				removeEntry.setAssociatedTargetEntry(associatedTenant);
				if (null == associatedTenant)
				{
					removeEntry.setAssociatedTargetEntry(associatedPayee);
					lRemovedPayees.add(removeEntry);
				}
				if (removeEntry.getNth() < removeEntry.getAssociatedTargetEntry().getNth())
				{
					throw new InvalidLedgerException("REMOVE entry (" + removeEntry.getNth() + ") positioned after target entry (" + removeEntry.getAssociatedTargetEntry().getNth() + ") in ledger.");
				}
			}
			else if (entry instanceof FlushEntry)
			{
				FlushEntry flushEntry = (FlushEntry)entry;
				String strFromTenantName = flushEntry.getFromTenantName();
				String strToTenantName = flushEntry.getToTenantName();
				if (!StringUtil.isDefined(strFromTenantName))
				{
					throw new InvalidLedgerException("FLUSH entry exists with empty \"from\" tenant name. Date: " + flushEntry.getDateString());
				}
				if (!StringUtil.isDefined(strToTenantName))
				{
					throw new InvalidLedgerException("FLUSH entry exists with empty \"to\" tenant name. Date: " + flushEntry.getDateString());
				}
				TenantEntry associatedFromTenant = getTenantEntry(lEntries, strFromTenantName);
				if (null == associatedFromTenant)
				{
					throw new InvalidLedgerException("Unresolvable \"from\" tenant name \"" + strFromTenantName + "\" in FLUSH entry. Date: " + flushEntry.getDateString());
				}
				TenantEntry associatedToTenant = getTenantEntry(lEntries, strToTenantName);
				if (null == associatedToTenant)
				{
					throw new InvalidLedgerException("Unresolvable \"to\" tenant name \"" + strToTenantName + "\" in FLUSH entry. Date: " + flushEntry.getDateString());
				}
				if (flushEntry.getNth() < associatedFromTenant.getNth())
				{
					throw new InvalidLedgerException("FLUSH entry (" + flushEntry.getNth() + ") positioned after \"from\" TENANT entry (" + associatedFromTenant.getNth() + ") in ledger.");
				}
				if (flushEntry.getNth() < associatedToTenant.getNth())
				{
					throw new InvalidLedgerException("FLUSH entry (" + flushEntry.getNth() + ") positioned after \"to\" TENANT entry (" + associatedToTenant.getNth() + ") in ledger.");
				}
				flushEntry.setAssociatedFromTenantEntry(associatedFromTenant);
				flushEntry.setAssociatedToTenantEntry(associatedToTenant);
			}
			else if (entry instanceof PayeeEntry)
			{
				PayeeEntry payeeEntry = (PayeeEntry)entry;
				RemoveEntry removeEntry = getRemoveEntry(lRemovedPayees, payeeEntry.getPayeeName());
				if (null != removeEntry)
				{	// Previously removed payee must have been re-activated
					lRemovedPayees.remove(removeEntry);
				}
			}
		}
		
		// PAYMENT entries must have a start and end dates and the start date must
		// be before the end date in time.
		for (Entry entry : lEntries)
		{
			if (entry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)entry;
				if (0 < paymentEntry.getStartDate().compareTo(paymentEntry.getEndDate()))
				{
					throw new InvalidLedgerException("PAYMENT entry exists where start date is AFTER the end date: " + paymentEntry.getAmount());
				}
			}
		}
	}

/*	
	private GenericDouble<Calendar, Calendar> getDateDomain(List<Entry> lEntries)
	{
		Set<Calendar> setDates = new HashSet<Calendar>();
		for (Entry entry : lEntries)
		{
			setDates.add(entry.getDate());
			if (entry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)entry;
				setDates.add(paymentEntry.getStartDate());
				setDates.add(paymentEntry.getEndDate());
			}
		}
		List<Calendar> lDates = new ArrayList<Calendar>(setDates);
		Collections.sort(lDates);
		
		return new GenericDouble<Calendar, Calendar>(lDates.get(0), lDates.get(lDates.size() - 1));
	}
	
	private void expandLedger(Calendar startDate, Calendar endDate, List<LedgerEntry> lDestinationForEntries)
	{
		Calendar currentDate = Calendar.getInstance();
		currentDate.setTime(startDate.getTime());
		int iNth = 0;
		while (0 >= currentDate.compareTo(endDate))
		{
			lDestinationForEntries.add(new LedgerEntry(currentDate, iNth++));
			Calendar nextDate = Calendar.getInstance();
			nextDate.setTime(currentDate.getTime());
			nextDate.add(Calendar.HOUR, 24);
			nextDate.set(Calendar.HOUR, 0);
			nextDate.set(Calendar.SECOND, 0);
			nextDate.set(Calendar.MILLISECOND, 0);
			currentDate = nextDate;
			//System.out.println("Current Date: " + DateUtil.getTime(DateTimeUtil.DF_YMDHMS, currentDate.getTimeInMillis()));
			//System.out.println("  End Date: " + DateUtil.getTime(DateTimeUtil.DF_YMDHMS, dateDomain.second.getTimeInMillis()));
		}
		System.out.println("Ledger has (" + m_lEntries.size() + ") days, starting on " + DateUtil.getTime(DateTimeUtil.DF_YMD, m_lEntries.get(0).getDate().getTimeInMillis()) + " and ending on " + DateTimeUtil.getTime(DateTimeUtil.DF_YMD, m_lEntries.get(m_lEntries.size()-1).getDate().getTimeInMillis()));
	}
	
	private void populateLedger(List<LedgerEntry> lLedgerEntries, List<Entry> lEntries)
	{
		for (LedgerEntry ledgerEntry : lLedgerEntries)
		{	// Add entries that can be added using a linear iteration thru the lists.
			Calendar date = ledgerEntry.getDate();
			for (Entry entry : lEntries)
			{
				if (entry instanceof DepositEntry)
				{
					DepositEntry depositEntry = (DepositEntry)entry;
					if (Entry.isSameMonthDayYear(depositEntry.getDate(), date))
					{
						ledgerEntry.addDeposit(depositEntry);
					}
				}
				else if (entry instanceof BalanceEntry)
				{
					BalanceEntry balanceEntry = (BalanceEntry)entry;
					if (Entry.isSameMonthDayYear(balanceEntry.getDate(), date))
					{
						ledgerEntry.addBalance(balanceEntry);
					}
				}
				else if (entry instanceof TenantEntry)
				{
					TenantEntry tenantEntry = (TenantEntry)entry;
					if (Entry.isSameMonthDayYear(tenantEntry.getDate(), date))
					{
						ledgerEntry.addTenant(tenantEntry);
					}
				}
				else if (entry instanceof PayeeEntry)
				{
					PayeeEntry payeeEntry = (PayeeEntry)entry;
					if (Entry.isSameMonthDayYear(payeeEntry.getDate(), date))
					{
						ledgerEntry.addPayee(payeeEntry);
					}
				}
				else if (entry instanceof MoveOutEntry)
				{
					MoveOutEntry moveOutEntry = (MoveOutEntry)entry;
					if (Entry.isSameMonthDayYear(moveOutEntry.getDate(), date))
					{
						ledgerEntry.addMoveOut(moveOutEntry);
					}
				}
			}
		}
		
		// Process all of the Payment entries
		for (Entry entry : lEntries)
		{
			if (entry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)entry;
				for (LedgerEntry ledgerEntry : lLedgerEntries)
				{
					Calendar date = ledgerEntry.getDate();
					if (paymentEntry.inServiceOn(date))
					{
						ledgerEntry.addInServicePayment(paymentEntry);
					}
					if (Entry.isSameMonthDayYear(paymentEntry.getDate(), date))
					{
						ledgerEntry.addPayment(paymentEntry);
					}
				}
			}
		}
	}
*/	
	public static TenantEntry getTenantEntry(List<Entry> lEntries, String strTenantName)
	{
		for (Entry entry : lEntries)
		{
			if (entry instanceof TenantEntry)
			{
				TenantEntry tenantEntry = (TenantEntry)entry;
				if (strTenantName.equals(tenantEntry.getTenantName()))
				{
					return tenantEntry;
				}
			}
		}
		return null;
	}
	
	public static PayeeEntry getPayeeEntry(List<? extends Entry> lEntries, String strPayeeName)
	{
		for (Entry entry : lEntries)
		{
			if (entry instanceof PayeeEntry)
			{
				PayeeEntry payeeEntry = (PayeeEntry)entry;
				if (strPayeeName.equals(payeeEntry.getPayeeName()))
				{
					return payeeEntry;
				}
			}
		}
		return null;
	}
	
	public static RemoveEntry getRemoveEntry(List<RemoveEntry> lEntries, String strTargetName)
	{
		for (RemoveEntry entry : lEntries)
		{
			if (strTargetName.equals(entry.getTargetName()))
			{
				return entry;
			}
		}
		return null;
	}
	
	public static MoveOutEntry getMoveOutTenantEntry(List<Entry> lEntries, String strTenantName)
	{
		for (Entry entry : lEntries)
		{
			if (entry instanceof MoveOutEntry)
			{
				MoveOutEntry moveOutEntry = (MoveOutEntry)entry;
				if (strTenantName.equals(moveOutEntry.getTenantName()))
				{
					return moveOutEntry;
				}
			}
		}
		return null;
	}
	
	public static LedgerEntry getMoveOutTenantLedgerEntry(List<LedgerEntry> lEntries, String strTenantName)
	{
		for (LedgerEntry entry : lEntries)
		{
			List<MoveOutEntry> lMoveOuts = entry.getActiveMoveOuts();
			if ((null != lMoveOuts) && (!lMoveOuts.isEmpty()))
			{
				for (MoveOutEntry moveOutEntry : lMoveOuts)
				{
					if (strTenantName.equals(moveOutEntry.getTenantName()))
					{
						return entry;
					}
				}
			}
		}
		return null;
	}
	
	public static RemoveEntry getRemovePayeeEntry(List<Entry> lEntries, String strPayeeName)
	{
		for (Entry entry : lEntries)
		{
			if (entry instanceof RemoveEntry)
			{
				RemoveEntry removeEntry = (RemoveEntry)entry;
				if (strPayeeName.equals(removeEntry.getTargetName()))
				{
					return removeEntry;
				}
			}
		}
		return null;
	}

	private Collection<Entry> getAllOnDate(List<Entry> lEntries, Verb verb, Calendar date)
	{
		Collection<Entry> cResults = new ArrayList<Entry>();
	
		for (Entry entry : lEntries)
		{
			if (entry.isOn(date))
			{
				if (entry.getVerb() == verb)
				{
					cResults.add(entry);
				}
			}
		}
		return cResults;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getIndent(iIndent)).append(getClass().getSimpleName()).append("\n");
		StringUtil.toString(sb, "Entry", m_lEntries, iIndent + 1);
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
}
