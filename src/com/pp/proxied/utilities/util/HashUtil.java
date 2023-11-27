package com.pp.proxied.utilities.util;

public class HashUtil
{
	private static final int PRIME = 23;
	
	public static int hash(int code, Object value)
	{
		return (code * PRIME) + (value != null ? value.hashCode() : 0);
	}
}
