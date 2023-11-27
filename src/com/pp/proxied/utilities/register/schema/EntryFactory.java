package com.pp.proxied.utilities.register.schema;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.List;

import com.pp.proxied.utilities.util.StringUtil;

public class EntryFactory
{
	private static final int ENTRY_PART_DATE_IDX = 0;
	private static final int ENTRY_PART_VERB_IDX = 1;
	private static final int ENTRY_PART_PAYEE_IDX = 2;
	private static final int ENTRY_PART_TENANTNAME_IDX = 2;
	private static final int ENTRY_PART_PAYEENAME_IDX = 2;
	private static final int ENTRY_PART_FROMTENANTNAME_IDX = 2;
	private static final int ENTRY_PART_TOTENANTNAME_IDX = 3;
	private static final int ENTRY_PART_AMOUNT_IDX = 3;
	private static final int ENTRY_PART_PAYEEABBREVIATION_IDX = 3;
	private static final int ENTRY_PART_STARTDATE_IDX = 4;
	private static final int ENTRY_PART_ENDDATE_IDX = 5;
	
	private static final int MINIMUM_ENTRY_PARTS = 3;
	
	public static RegisterBaseEntry getEntry(String strEntry)
		throws ParseException, InvalidParameterException
	{
		List<String> lEntryParts = StringUtil.divideString(strEntry, ",", false);
		if (MINIMUM_ENTRY_PARTS > lEntryParts.size())
		{
			throw new InvalidParameterException("Invalid entry. Too few parts: " + strEntry);
		}
		
		// Trim all parts
		for (int i=0; i<lEntryParts.size(); i++)
		{
			lEntryParts.set(i, lEntryParts.get(i).trim());
		}
		
		Verb verb = Verb.from(lEntryParts.get(ENTRY_PART_VERB_IDX));
		
		switch (verb)
		{
		case BALANCE:
				if (Verb.BALANCE.getPartsCount() != lEntryParts.size())
				{
					throw new InvalidParameterException("Invalid BALANCE entry. Invalid parts count. Expected: " + Verb.BALANCE.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
				}
				return new BalanceEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
						                lEntryParts.get(ENTRY_PART_TENANTNAME_IDX),
						                lEntryParts.get(ENTRY_PART_AMOUNT_IDX));
		case TENANT:
			if (Verb.TENANT.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid TENANT entry. Invalid parts count. Expected: " + Verb.TENANT.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new TenantEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
					               lEntryParts.get(ENTRY_PART_TENANTNAME_IDX));
		case MOVEOUT:
			if (Verb.MOVEOUT.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid MOVEOUT entry. Invalid parts count. Expected: " + Verb.MOVEOUT.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new MoveOutEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
					                lEntryParts.get(ENTRY_PART_TENANTNAME_IDX));
		case PAYEE:
			if (Verb.PAYEE.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid PAYEE entry. Invalid parts count. Expected: " + Verb.PAYEE.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new PayeeEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
								  lEntryParts.get(ENTRY_PART_PAYEENAME_IDX),
					              lEntryParts.get(ENTRY_PART_PAYEEABBREVIATION_IDX));
		case DEPOSIT:
			if (Verb.DEPOSIT.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid DEPOSIT entry. Invalid parts count. Expected: " + Verb.DEPOSIT.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new DepositEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
					                lEntryParts.get(ENTRY_PART_TENANTNAME_IDX),
					                lEntryParts.get(ENTRY_PART_AMOUNT_IDX));
		case PAYMENT:
			if (Verb.PAYMENT.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid PAYMENT entry. Invalid parts count. Expected: " + Verb.PAYMENT.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new PaymentEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
					                lEntryParts.get(ENTRY_PART_PAYEE_IDX),
					                lEntryParts.get(ENTRY_PART_AMOUNT_IDX),
					                lEntryParts.get(ENTRY_PART_STARTDATE_IDX),
					                lEntryParts.get(ENTRY_PART_ENDDATE_IDX));
		case REMOVE:
			if (Verb.REMOVE.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid REMOVE entry. Invalid parts count. Expected: " + Verb.REMOVE.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new RemoveEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
					               lEntryParts.get(ENTRY_PART_TENANTNAME_IDX));
		case FLUSH:
			if (Verb.FLUSH.getPartsCount() != lEntryParts.size())
			{
				throw new InvalidParameterException("Invalid FLUSH entry. Invalid parts count. Expected: " + Verb.FLUSH.getPartsCount() + ", Got: " + lEntryParts.size() + ", " + strEntry);
			}
			return new FlushEntry(lEntryParts.get(ENTRY_PART_DATE_IDX),
					                lEntryParts.get(ENTRY_PART_FROMTENANTNAME_IDX),
					                lEntryParts.get(ENTRY_PART_TOTENANTNAME_IDX));
		}
		throw new InvalidParameterException("Unrecognized Verb: " + strEntry);
	}
}
