package com.pp.proxied.utilities.schema.comparators;

import java.util.Comparator;

import com.pp.proxied.utilities.schema.TenantEntry;

public class TenantEntryByTenantName
	implements Comparator<TenantEntry>
{
	// Sort TenantEntry instances ONLY by tenant name
	public int compare(TenantEntry thisOne, TenantEntry thatOne)
	{
		if ((null != thisOne.getTenantName()) && (null == thatOne.getTenantName()))
		{
			return 1;
		}
		if ((null == thisOne.getTenantName()) && (null != thatOne.getTenantName()))
		{
			return -1;
		}
		return thisOne.getTenantName().compareTo(thatOne.getTenantName());
	}
}

