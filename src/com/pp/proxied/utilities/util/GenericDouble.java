package com.pp.proxied.utilities.util;

public class GenericDouble<F, S>
{
    public final F first;
    public final S second;

    public GenericDouble(F first, S second)
    {
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object that)
    {
        if (!(that instanceof GenericDouble))
        {
            return false;
        }
        GenericDouble<?, ?> p = (GenericDouble<?, ?>) that;
        if (first != p.first)
        {
        	if ((null == first) || (null == p.first))
        	{	// One is null and the other not null. Not equal.
        		return false;
        	}
        	// Both non-null references
        	if (!first.equals(p.first))
        	{
        		return false;
        	}
        }
        // The firsts are equal. Now try the seconds.
        if (second != p.second)
        {
        	if ((null == second) || (null == p.second))
        	{	// One is null and the other not null. Not equal.
        		return false;
        	}
        	// Both non-null references
        	if (!second.equals(p.second))
        	{
        		return false;
        	}
        }
        return true;
    }
    
    @Override
    public int hashCode()
    {
    	int code = 37;
    	code = HashUtil.hash(code, first);
    	code = HashUtil.hash(code, second);
    	return code;
    }
}

