package com.pp.proxied.utilities.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
    
    public static boolean equals(Map<?, ?> one, Map<?, ?> two)
    {
    	if (one == two)
    	{
    		return true;
    	}
    	if (one == null || two == null)
    	{
    		return false;
    	}
    	return one.equals(two);
    }
    
    public static boolean equalsIgnoreOrder(Map<?,?> one, Map<?, ?> two)
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
    	for (Map.Entry<?, ?> entry : one.entrySet())
    	{
    		if (!ObjectUtil.areReferencesEqual(entry.getValue(), two.get(entry.getKey())))
    		{
    			return false;
    		}
    	}
    	return true;
    }
}
