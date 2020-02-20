package com.pp.proxied.utilities.util;

public class ObjectUtil
{
	public static boolean areReferencesEqual(Object thisObject, Object thatObject)
	{
		if (thisObject == thatObject)
		{ // (null, null) OR (X,X)
			return true;
		}
		// (null, X) OR (X, null) OR (X, Y)
		else if ((null != thisObject) && (null != thatObject))
		{ // (X, Y)
			return thisObject.equals(thatObject);
		}
		// (null, X) OR (X, null)
		return false;
	}
}
