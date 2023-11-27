package com.pp.proxied.utilities.ledger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.schema.RegisterRemoves;
import com.pp.proxied.utilities.register.FullDateBoundsProcessor;
import com.pp.proxied.utilities.register.Register;
import com.pp.proxied.utilities.register.RegisterEntry;
import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.GenericDouble;

public class Ledger
{
	private Register m_register;
	private List<LedgerEntry> m_lLedgerEntries;
	
	public Ledger(Register register)
	{
		m_register = register;
		int iNthLedgerEntry = 0;
		m_lLedgerEntries = new ArrayList<LedgerEntry>();
		// Get the bounding dates for the register and create a ledger covering
		// all those dates.
		GenericDouble<Calendar, Calendar> ledgerDateBounds = new FullDateBoundsProcessor().process(register);
		
		// Fill in with placeholders from the earliest date to the first entry.
		// Validation will not allow an entry to have a date that preceeds the
		// first ledger entry.
		List<RegisterBaseEntry> lRegisterEntries = m_register.getEntries();
		Calendar cursor = ledgerDateBounds.first;
		while (lRegisterEntries.get(0).isAfter(cursor))
		{
			LedgerEntry placeholder = new LedgerEntry(iNthLedgerEntry++, cursor);
			m_lLedgerEntries.add(placeholder);
			cursor = DateUtil.getNextDay(cursor);
		}
		
		RegisterBaseEntry previous = null;
		LedgerEntry currentLedgerEntry = null;

		for (RegisterBaseEntry registerBaseEntry : lRegisterEntries)
		{
			if (null != previous)
			{	// Fill in days that have no explicit entry in the ledger
				Calendar day = DateUtil.getNextDay(previous.getDate());
				while (registerBaseEntry.isAfter(day))
				{
					LedgerEntry placeholder = new LedgerEntry(iNthLedgerEntry++, day);
					m_lLedgerEntries.add(placeholder);
					day = DateUtil.getNextDay(day);
				}
			}
			if ((null == currentLedgerEntry) ||
				(0 != RegisterBaseEntry.compareMonthDayYear(registerBaseEntry.getDate(), currentLedgerEntry.getDate())))
			{
				currentLedgerEntry = new LedgerEntry(iNthLedgerEntry++, registerBaseEntry.getDate());
				m_lLedgerEntries.add(currentLedgerEntry);
			}
			if (registerBaseEntry instanceof DepositEntry)
			{
				currentLedgerEntry.addDeposit((DepositEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof BalanceEntry)
			{
				currentLedgerEntry.addBalance((BalanceEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof TenantEntry)
			{
				currentLedgerEntry.addTenant((TenantEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof PayeeEntry)
			{
				currentLedgerEntry.addPayee((PayeeEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof MoveOutEntry)
			{
				currentLedgerEntry.addMoveOut((MoveOutEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof PaymentEntry)
			{
				currentLedgerEntry.addPayment((PaymentEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof RemoveEntry)
			{
				currentLedgerEntry.addRemove((RemoveEntry)registerBaseEntry);
			}
			else if (registerBaseEntry instanceof FlushEntry)
			{
				currentLedgerEntry.addFlush((FlushEntry)registerBaseEntry);
			}
			previous = registerBaseEntry;
		}
		// If after filling out the ledger, there are later dates referenced
		// in some entries, fill out the ledger to cover the latest discovered
		// date.
		int iNthLedgerEntryOriginal = iNthLedgerEntry;
		cursor = DateUtil.getNextDay(m_lLedgerEntries.get(m_lLedgerEntries.size() - 1).getDate());
		while (m_lLedgerEntries.get(m_lLedgerEntries.size() - 1).isOnOrBefore(ledgerDateBounds.second))
		{
			LedgerEntry placeholder = new LedgerEntry(iNthLedgerEntry++, cursor);
			m_lLedgerEntries.add(placeholder);
			cursor = DateUtil.getNextDay(cursor);
		}
		
		setEntryReferences(m_lLedgerEntries);

		new ActiveTenantProcessor().process(this);
		new ActivePaymentProcessor().process(this);
		new DailyPaymentsProcessor().process(this);
		new BalanceProcessor().process(this);
	}
	
	public List<LedgerEntry> getLedgerEntries()
	{
		return m_lLedgerEntries;
	}
	
	public GenericDouble<Calendar, Calendar> getBoundingDates()
	{
		return new GenericDouble<Calendar, Calendar>(m_lLedgerEntries.get(0).getDate(), m_lLedgerEntries.get(m_lLedgerEntries.size() - 1).getDate());
	}
	
	private void setEntryReferences(List<LedgerEntry> lEntries)
	{
		LedgerEntry previous = null;
		for (int i=0; i<lEntries.size(); i++)
		{
			LedgerEntry current = lEntries.get(i);
			LedgerEntry next = null;
			boolean bAtFirst = (0 == i);
			boolean bAtLast = (i == (lEntries.size() - 1));
			if (!bAtLast)
			{
				next = lEntries.get(i + 1);
				if (bAtFirst)
				{	// First entry
					current.setNextEntry(next);
				}
				else
				{	// Nth entry
					current.setPreviousEntry(previous);
					current.setNextEntry(next);
				}
			}
			else
			{	// Last Entry
				current.setPreviousEntry(previous);
			}
			previous = current;
		}
	}
	
	public RemoveEntry getRemovePayeeEntry(PayeeEntry payee)
	{
		List<LedgerEntry> lEntries = getLedgerEntries();
		if (null != lEntries)
		{
			for (LedgerEntry ledgerEntry : lEntries)
			{
				RegisterRemoves registerRemoves = ledgerEntry.getRegisterRemoves();
				if ((null != registerRemoves) && (!registerRemoves.isEmpty()))
				{
					List<RemoveEntry> lRemoves = registerRemoves.getRemoves();
					for (RemoveEntry candidate : lRemoves)
					{
						RegisterBaseEntry candidateEntry = candidate.getAssociatedTargetEntry();
						if (candidateEntry instanceof PayeeEntry)
						{
							if (payee.getPayeeName().equals(candidate.getTargetName()))
							{
								return candidate;
							}
						}
					}
				}
			}
		}
		return null;
	}

	public static TenantEntry getTenantEntry(List<RegisterBaseEntry> lEntries, String strTenantName)
	{
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof TenantEntry)
			{
				TenantEntry tenantEntry = (TenantEntry)registerBaseEntry;
				if (strTenantName.equals(tenantEntry.getTenantName()))
				{
					return tenantEntry;
				}
			}
		}
		return null;
	}
	
	public static PayeeEntry getPayeeEntry(List<? extends RegisterBaseEntry> lEntries, String strPayeeName)
	{
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof PayeeEntry)
			{
				PayeeEntry payeeEntry = (PayeeEntry)registerBaseEntry;
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
	
	public static MoveOutEntry getMoveOutTenantEntry(List<RegisterBaseEntry> lEntries, String strTenantName)
	{
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof MoveOutEntry)
			{
				MoveOutEntry moveOutEntry = (MoveOutEntry)registerBaseEntry;
				if (strTenantName.equals(moveOutEntry.getTenantName()))
				{
					return moveOutEntry;
				}
			}
		}
		return null;
	}
	
	public static RegisterEntry getMoveOutTenantLedgerEntry(List<RegisterEntry> lEntries, String strTenantName)
	{
		for (RegisterEntry entry : lEntries)
		{
			List<MoveOutEntry> lMoveOuts = entry.getRegistryMoveOuts();
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
	
	public static RemoveEntry getRemovePayeeEntry(List<RegisterBaseEntry> lEntries, String strPayeeName)
	{
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof RemoveEntry)
			{
				RemoveEntry removeEntry = (RemoveEntry)registerBaseEntry;
				if (strPayeeName.equals(removeEntry.getTargetName()))
				{
					return removeEntry;
				}
			}
		}
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (LedgerEntry entry : getLedgerEntries())
		{
			sb.append(entry.toString());
		}
		return sb.toString();
	}
}
