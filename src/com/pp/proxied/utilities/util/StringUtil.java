package com.pp.proxied.utilities.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{
	public static boolean isDefined(String strCandidate)
	{
		return ((null != strCandidate) && (0 != strCandidate.length()));
	}
	
	public static String getSpaces(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<iIndent; i++)
		{
			sb.append("    ");
		}
		return sb.toString();
	}
	
	public static boolean areReferencesEqual(String strOne, String strTwo)
	{
		//references equal, or are both null?
		if (strOne == strTwo)
		{
			return true;
		}
		return strOne != null && strOne.equals(strTwo);
	}
	
	public static List<String> divideString(String strSource, String strDelimiter, boolean bReturnDelimiter)
	{
		List<String> lDivisions = new ArrayList<String>();
		String str = new String(strSource);
		while (true)
		{
		    int iIdx = str.indexOf(strDelimiter);
		    if (-1 != iIdx)
		    {
		    	String strThis = str.substring(0, iIdx);
		    	if (0 != strThis.length())
		    	{
		    		lDivisions.add(strThis);
		    	}
		    	if (bReturnDelimiter)
		    	{
		    		lDivisions.add(strDelimiter);
		    	}
		    	str = str.substring(iIdx + strDelimiter.length());
		    }
		    else
		    {	// Add last string only if it is non-empty.
		    	if (StringUtil.isDefined(str))
		    	{
		    		lDivisions.add(str);
		    	}
		    	break;
		    }
		}
		return lDivisions;
	}
	
    public static String replaceSubString(String strTarget, String strMatchSubString, String strReplacement)
    {
    	int iIdx = 0;
    	while (-1 != (iIdx = (strTarget.indexOf(strMatchSubString))))
    	{
    		String strFrontPart = strTarget.substring(0, iIdx);
    		String strBackPart = strTarget.substring(iIdx + strMatchSubString.length());
    		strTarget = strFrontPart + strReplacement + strBackPart;
    	}
    	return strTarget;
    }
}
