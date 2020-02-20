package com.pp.proxied.utilities.util;

public class GenericTriple<F, S, T>
	extends GenericDouble<F, S>
{
	public final T third;
	
	public GenericTriple(F first, S second, T third)
	{
		super(first, second);
		this.third = third;
	}
	
	@Override
	public boolean equals(Object that)
	{
	    if (!(that instanceof GenericTriple))
	    {
	        return false;
	    }
		if (!super.equals(that))
		{
			return false;
		}
		GenericTriple<?, ?, ?> p = (GenericTriple<?, ?, ?>) that;
	    if (third != p.third)
	    {
	    	if ((null == third) || (null == p.third))
	    	{	// One is null and the other not null. Not equal.
	    		return false;
	    	}
	    	// Both non-null references
	    	if (!third.equals(p.third))
	    	{
	    		return false;
	    	}
	    }
	    return true;
	}
	
	@Override
	public int hashCode()
	{
		int code = super.hashCode();
    	code = code * 37 + (third != null ? third.hashCode() : 0);
		return code;
	}
}
