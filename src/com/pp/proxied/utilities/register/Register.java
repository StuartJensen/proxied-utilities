package com.pp.proxied.utilities.register;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.GenericDouble;
import com.pp.proxied.utilities.util.StringUtil;

public class Register
{
	private List<RegisterBaseEntry> m_lEntries;
	
	public Register(List<RegisterBaseEntry> lEntries)
		throws InvalidRegisterEntryException
	{
		m_lEntries = lEntries;
		
		setEntryReferences(m_lEntries);
		validate(m_lEntries);
	}
	
	private void setEntryReferences(List<RegisterBaseEntry> lEntries)
	{
		RegisterBaseEntry previous = null;
		for (int i=0; i<lEntries.size(); i++)
		{
			RegisterBaseEntry current = lEntries.get(i);
			RegisterBaseEntry next = null;
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
	
	public List<RegisterBaseEntry> getEntries()
	{
		return m_lEntries;
	}
	
	public GenericDouble<Calendar, Calendar> getBoundingDates()
	{
		return new GenericDouble<Calendar, Calendar>(m_lEntries.get(0).getDate(), m_lEntries.get(m_lEntries.size() - 1).getDate());
	}
	
	private void validate(List<RegisterBaseEntry> lEntries)
		throws InvalidRegisterEntryException
	{
		// Enforce non-empty AND unique tenant and payee names
		Collection<String> cTenantNames = new ArrayList<String>();
		Collection<String> cPayeeNames = new ArrayList<String>();
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof TenantEntry)
			{
				TenantEntry tenantEntry = (TenantEntry)registerBaseEntry;
				String strTenantName = tenantEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidRegisterEntryException("TENANT entry exists with empty tenant name. Date: " + tenantEntry.getDateString());
				}
				if (cTenantNames.contains(strTenantName))
				{
					throw new InvalidRegisterEntryException("Duplicate TENANT entry. Date: " + tenantEntry.getDateString());
				}
				cTenantNames.add(strTenantName);
			}
			else if (registerBaseEntry instanceof PayeeEntry)
			{
				PayeeEntry payeeEntry = (PayeeEntry)registerBaseEntry;
				String strPayeeName = payeeEntry.getPayeeName();
				if (!StringUtil.isDefined(strPayeeName))
				{
					throw new InvalidRegisterEntryException("PAYEE entry exists with empty payee name. Date: " + payeeEntry.getDateString());
				}
				if (cPayeeNames.contains(strPayeeName))
				{
					throw new InvalidRegisterEntryException("Duplicate PAYEE entry. Date: " + payeeEntry.getDateString());
				}
				cPayeeNames.add(strPayeeName);
			}
		}
		
		// Validate all tenant and payee names in BALANCE, PAYMENT, DEPOSIT, MOVEOUT, REMOVE, FLUSH are resolvable
		List<RemoveEntry> lRemovedPayees = new ArrayList<RemoveEntry>();
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof BalanceEntry)
			{
				BalanceEntry balanceEntry = (BalanceEntry)registerBaseEntry;
				String strTenantName = balanceEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidRegisterEntryException("BALANCE entry exists with empty tenant name. Date: " + balanceEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTenantName);
				if (null == associatedTenant)
				{
					throw new InvalidRegisterEntryException("Unresolvable tenant name \"" + strTenantName + "\" in BALANCE entry. Date: " + balanceEntry.getDateString());
				}
				if (balanceEntry.getNth() < associatedTenant.getNth())
				{
					throw new InvalidRegisterEntryException("BALANCE entry (" + balanceEntry.getNth() + ") positioned before TENANT entry (" + associatedTenant.getNth() + ") in ledger.");
				}
				balanceEntry.setAssociatedTenantEntry(associatedTenant);
			}
			else if (registerBaseEntry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)registerBaseEntry;
				String strPayeeName = paymentEntry.getPayeeName();
				if (!StringUtil.isDefined(strPayeeName))
				{
					throw new InvalidRegisterEntryException("PAYMENT entry exists with empty payee name. Date: " + paymentEntry.getDateString());
				}
				if (0 < RegisterBaseEntry.compareMonthDayYear(paymentEntry.getStartDate(), paymentEntry.getEndDate()))
				{
					throw new InvalidRegisterEntryException("PAYMENT entry exists with end date AFTER start date. Date: " + paymentEntry.getDateString());
				}
				PayeeEntry associatedPayee = getPayeeEntry(lEntries, strPayeeName);
				if (null == associatedPayee)
				{
					throw new InvalidRegisterEntryException("Unresolvable payee name \"" + strPayeeName + "\" in PAYMENT entry. Date: " + paymentEntry.getDateString());
				}
				if (paymentEntry.getNth() < associatedPayee.getNth())
				{
					throw new InvalidRegisterEntryException("PAYMENT entry (" + paymentEntry.getNth() + ") positioned before PAYEE entry (" + associatedPayee.getNth() + ") in ledger.");
				}
				if (0 > RegisterBaseEntry.compareMonthDayYear(paymentEntry.getStartDate(), associatedPayee.getDate()))
				{
					throw new InvalidRegisterEntryException("PAYMENT entry (" + paymentEntry.getDateString() + ") with service start date (" + RegisterBaseEntry.toString(paymentEntry.getStartDate()) + ") before PAYEE entry (" + associatedPayee.getDateString() + ") in ledger.");
				}
				paymentEntry.setAssociatedPayeeEntry(associatedPayee);
				// Ensure this payment service time period falls within an "active" payee
				RemoveEntry removedEntry = getRemoveEntry(lRemovedPayees, associatedPayee.getPayeeName());
				if (null != removedEntry)
				{	// Yes, this payee was removed
					if (removedEntry.isAfter(paymentEntry.getEndDate()))
					{
						throw new InvalidRegisterEntryException("PAYMENT entry (" + paymentEntry.getDateString() + ") has a service end date that extends after a Payee (" + associatedPayee.getPayeeName() + ") was removed on (" + removedEntry.getDateString() + ")\n");
					}
				}
			}
			else if (registerBaseEntry instanceof DepositEntry)
			{
				DepositEntry depositEntry = (DepositEntry)registerBaseEntry;
				String strTenantName = depositEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidRegisterEntryException("DEPOSIT entry exists with empty tenant name. Date: " + depositEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTenantName);
				if (null == associatedTenant)
				{
					throw new InvalidRegisterEntryException("Unresolvable tenant name \"" + strTenantName + "\" in DEPOSIT entry. Date: " + depositEntry.getDateString());
				}
				if (depositEntry.getNth() < associatedTenant.getNth())
				{
					throw new InvalidRegisterEntryException("DEPOSIT entry (" + depositEntry.getNth() + ") positioned before TENANT entry (" + associatedTenant.getNth() + ") in ledger.");
				}
				depositEntry.setAssociatedTenantEntry(associatedTenant);
			}
			else if (registerBaseEntry instanceof MoveOutEntry)
			{
				MoveOutEntry moveOutEntry = (MoveOutEntry)registerBaseEntry;
				String strTenantName = moveOutEntry.getTenantName();
				if (!StringUtil.isDefined(strTenantName))
				{
					throw new InvalidRegisterEntryException("MOVEOUT entry exists with empty tenant name. Date: " + moveOutEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTenantName);
				if (null == associatedTenant)
				{
					throw new InvalidRegisterEntryException("Unresolvable tenant name \"" + strTenantName + "\" in MOVEOUT entry. Date: " + moveOutEntry.getDateString());
				}
				if (moveOutEntry.getNth() < associatedTenant.getNth())
				{
					throw new InvalidRegisterEntryException("MOVEOUT entry (" + moveOutEntry.getNth() + ") positioned after TENANT entry (" + associatedTenant.getNth() + ") in ledger.");
				}
				moveOutEntry.setAssociatedTenantEntry(associatedTenant);
			}
			else if (registerBaseEntry instanceof RemoveEntry)
			{
				RemoveEntry removeEntry = (RemoveEntry)registerBaseEntry;
				String strTargetName = removeEntry.getTargetName();
				if (!StringUtil.isDefined(strTargetName))
				{
					throw new InvalidRegisterEntryException("REMOVE entry exists with empty target name. Date: " + removeEntry.getDateString());
				}
				TenantEntry associatedTenant = getTenantEntry(lEntries, strTargetName);
				PayeeEntry associatedPayee = getPayeeEntry(lEntries, strTargetName);
				if ((null == associatedTenant) && (null == associatedPayee))
				{
					throw new InvalidRegisterEntryException("Unresolvable target name \"" + strTargetName + "\" in REMOVE entry. No matching tenant nor payee. Date: " + removeEntry.getDateString());
				}
				if ((null != associatedTenant) && (null != associatedPayee))
				{
					throw new InvalidRegisterEntryException("Multiple resolutions for target name \"" + strTargetName + "\" in REMOVE entry. Both tenant and payee match. Date: " + removeEntry.getDateString());
				}
				removeEntry.setAssociatedTargetEntry(associatedTenant);
				if (null == associatedTenant)
				{
					removeEntry.setAssociatedTargetEntry(associatedPayee);
					lRemovedPayees.add(removeEntry);
				}
				if (removeEntry.getNth() < removeEntry.getAssociatedTargetEntry().getNth())
				{
					throw new InvalidRegisterEntryException("REMOVE entry (" + removeEntry.getNth() + ") positioned after target entry (" + removeEntry.getAssociatedTargetEntry().getNth() + ") in ledger.");
				}
			}
			else if (registerBaseEntry instanceof FlushEntry)
			{
				FlushEntry flushEntry = (FlushEntry)registerBaseEntry;
				String strFromTenantName = flushEntry.getFromTenantName();
				String strToTenantName = flushEntry.getToTenantName();
				if (!StringUtil.isDefined(strFromTenantName))
				{
					throw new InvalidRegisterEntryException("FLUSH entry exists with empty \"from\" tenant name. Date: " + flushEntry.getDateString());
				}
				if (!StringUtil.isDefined(strToTenantName))
				{
					throw new InvalidRegisterEntryException("FLUSH entry exists with empty \"to\" tenant name. Date: " + flushEntry.getDateString());
				}
				TenantEntry associatedFromTenant = getTenantEntry(lEntries, strFromTenantName);
				if (null == associatedFromTenant)
				{
					throw new InvalidRegisterEntryException("Unresolvable \"from\" tenant name \"" + strFromTenantName + "\" in FLUSH entry. Date: " + flushEntry.getDateString());
				}
				TenantEntry associatedToTenant = getTenantEntry(lEntries, strToTenantName);
				if (null == associatedToTenant)
				{
					throw new InvalidRegisterEntryException("Unresolvable \"to\" tenant name \"" + strToTenantName + "\" in FLUSH entry. Date: " + flushEntry.getDateString());
				}
				if (flushEntry.getNth() < associatedFromTenant.getNth())
				{
					throw new InvalidRegisterEntryException("FLUSH entry (" + flushEntry.getNth() + ") positioned after \"from\" TENANT entry (" + associatedFromTenant.getNth() + ") in ledger.");
				}
				if (flushEntry.getNth() < associatedToTenant.getNth())
				{
					throw new InvalidRegisterEntryException("FLUSH entry (" + flushEntry.getNth() + ") positioned after \"to\" TENANT entry (" + associatedToTenant.getNth() + ") in ledger.");
				}
				flushEntry.setAssociatedFromTenantEntry(associatedFromTenant);
				flushEntry.setAssociatedToTenantEntry(associatedToTenant);
			}
			else if (registerBaseEntry instanceof PayeeEntry)
			{
				PayeeEntry payeeEntry = (PayeeEntry)registerBaseEntry;
				RemoveEntry removeEntry = getRemoveEntry(lRemovedPayees, payeeEntry.getPayeeName());
				if (null != removeEntry)
				{	// Previously removed payee must have been re-activated
					lRemovedPayees.remove(removeEntry);
				}
			}
		}
		
		// PAYMENT entries must have a start and end dates and the start date must
		// be before the end date in time.
		for (RegisterBaseEntry registerBaseEntry : lEntries)
		{
			if (registerBaseEntry instanceof PaymentEntry)
			{
				PaymentEntry paymentEntry = (PaymentEntry)registerBaseEntry;
				if (0 < paymentEntry.getStartDate().compareTo(paymentEntry.getEndDate()))
				{
					throw new InvalidRegisterEntryException("PAYMENT entry exists where start date is AFTER the end date: " + paymentEntry.getAmount());
				}
			}
		}
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

	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getSpaces(iIndent)).append(getClass().getSimpleName()).append("\n");
		if (null != m_lEntries)
		{
			for (RegisterBaseEntry entry : m_lEntries)
			{
				sb.append(entry.toString(iIndent + 1));
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
