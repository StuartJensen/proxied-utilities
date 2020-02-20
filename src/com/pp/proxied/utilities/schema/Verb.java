package com.pp.proxied.utilities.schema;

import java.security.InvalidParameterException;

public enum Verb {
	BALANCE(8, 4),
	TENANT(3, 3),
	PAYEE(4, 3),
	MOVEOUT(1, 3),
	DEPOSIT(5, 4),
	PAYMENT(6, 6),
	FLUSH(7, 4),
	REMOVE(2, 3);
	
	private int m_iSortOrder;
	private int m_iPartsCount;
	
	private Verb(int iSortOrder, int iPartsCount)
	{
		m_iSortOrder = iSortOrder;
		m_iPartsCount = iPartsCount;
	}
	
	public int getPartsCount()
	{
		return m_iPartsCount;
	}
	
	public int getSortOrder()
	{
		return m_iSortOrder;
	}
	
	public static Verb from(String strVerb)
		throws InvalidParameterException
	{
		Verb[] arValues = Verb.values();
		for (Verb verb : arValues)
		{
			if (verb.name().equals(strVerb))
			{
				return verb;
			}
		}
		throw new InvalidParameterException("Unrecognized Verb Specifier: " + strVerb);
	}
}
