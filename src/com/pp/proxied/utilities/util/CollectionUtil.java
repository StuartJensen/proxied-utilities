package com.pp.proxied.utilities.util;

import java.util.Collection;
import java.util.Iterator;

public class CollectionUtil
{
    public static boolean equals(Collection<?> one, Collection<?> two)
    {
    	if (one == two)
    	{
    		return true;
    	}
    	if (one == null || two == null)
    	{
    		return false;
    	}
    	if (one.size() != two.size())
    	{
    		return false;
    	}
    	Iterator<?> oneIt = one.iterator();
    	Iterator<?> twoIt = two.iterator();
    	while (oneIt.hasNext())
    	{
    		if (!oneIt.next().equals(twoIt.next()))
    		{
    			return false;
    		}
    	}
    	return true;
    }
}
