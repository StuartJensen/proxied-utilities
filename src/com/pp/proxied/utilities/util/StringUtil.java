package com.pp.proxied.utilities.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtil
{
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
    
	public static boolean isDefined(CharSequence value)
	{
		return null != value && value.length() > 0;
	}
	
	private static final String INDENT_NONE = "";
	private static final String INDENT_ONE = "   ";
	private static final String INDENT_TWO = INDENT_ONE + INDENT_ONE;
	private static final String INDENT_THREE = INDENT_TWO + INDENT_ONE;
	private static final String INDENT_FOUR = INDENT_THREE + INDENT_ONE;
	private static final String INDENT_FIVE = INDENT_FOUR + INDENT_ONE;
	private static final String INDENT_SIX = INDENT_FIVE + INDENT_ONE;

	public static String getIndent(int level)
	{
		int maskedLevel = level & 0x0ff;
		switch (maskedLevel)
		{
		case 0: return INDENT_NONE;
		case 1: return INDENT_ONE;
		case 2: return INDENT_TWO;
		case 3: return INDENT_THREE;
		case 4: return INDENT_FOUR;
		case 5: return INDENT_FIVE;
		case 6: return INDENT_SIX;
		}
		StringBuilder sb = new StringBuilder();
		while (maskedLevel-- > 0)
		{
			sb.append(INDENT_ONE);
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

	public static boolean areReferencesEqual(CharSequence one, CharSequence two)
	{
		//references equal, or are both null?
		if (one == two)
		{
			return true;
		}
		if (one == null)
		{
			return two == null;
		}
		//not null...
		if (one.length() != two.length())
		{
			return false;
		}
		for (int i = 0; i < one.length(); i++)
		{
			if (one.charAt(i) != two.charAt(i))
			{
				return false;
			}
		}
		return true;
	}

    public static final int MAX_VALUE_SIZE = 4096;
    public static final String VALUE_TRUNCATED = "[truncated]";
    
	public static void toString(StringBuilder sb, String label, Iterable<?> items, int iLevel)
	{
		if (items != null)
		{
			for (Object item : items)
			{
				toString(sb, label, item, iLevel);
			}
		}
	}
	
	public static void toString(StringBuilder sb, String label, Object value, int iLevel)
	{
		if (value != null)
		{
			String valueString = value.toString();
			if (StringUtil.isDefined(valueString))
			{
				sb.append(StringUtil.getIndent(iLevel)).append(label).append(": ");				
				if (valueString.length() > MAX_VALUE_SIZE)
				{
					sb.append(valueString, 0, MAX_VALUE_SIZE);
					sb.append(VALUE_TRUNCATED);
				}
				else
				{
					sb.append(valueString);
				}
				sb.append('\n');
			}
		}
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
	
    public static List<String> divideString(String strSource, String[] arDelimiters)
    {
    	return divideString(strSource, arDelimiters, true);
    }
		
    public static List<String> divideString(String strSource, String[] arDelimiters, boolean bReturnDelimiter)
    {
		ArrayList<String> alDivisions = new ArrayList<String>();
    	if (null == strSource)
    	{	// No source string, return an empty array
    		return alDivisions;
    	}
    	alDivisions.add(strSource);
    	if ((null == arDelimiters) || (0 == arDelimiters.length))
    	{	// No delimiters, return the original source string
    		return alDivisions;
    	}
    	return divideString(alDivisions, arDelimiters, bReturnDelimiter, 0);
    }
    
    private static List<String> divideString(List<String> lDivisions, String[] arDelimiters, boolean bReturnDelimiter, int iDelimiterIdx)
    {
    	if (iDelimiterIdx >= arDelimiters.length)
    	{	// If we have reached the end of the delimiters list, we are done
    		return lDivisions;
    	}
    	// Create a new List for the new set of divisions
    	ArrayList<String> lNewDivisions = new ArrayList<String>();
    	
    	for (int i=0; i<lDivisions.size(); i++)
    	{
    		if (!StringUtil.isStringIn(lDivisions.get(i), arDelimiters, false))
    		{	// This division is NOT a delimiter, process it
    			//
    			List<String> lTheseDivisions = divideString(lDivisions.get(i), arDelimiters[iDelimiterIdx], bReturnDelimiter);
        		lNewDivisions.addAll(lTheseDivisions);
        	}
    		else
    		{	// This division IS a delimiter, add it to the new divisions list
    			if (bReturnDelimiter)
    			{
    				lNewDivisions.add(lDivisions.get(i));
    			}
    		}
    	}
    	return divideString(lNewDivisions, arDelimiters, bReturnDelimiter, ++iDelimiterIdx);
    }

	public static boolean isStringIn(String strTarget, String[] arList, boolean bCaseIgnore)
	{
		if ((null != arList) && (0 != arList.length) && (null != strTarget))
		{
			for (int i=0; i<arList.length; i++)
			{
				if (bCaseIgnore)
				{
					if (strTarget.equalsIgnoreCase(arList[i]))
					{
						return true;
					}
				}
				else
				{
					if (strTarget.equals(arList[i]))
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public static boolean isNumericPlus(String strCandidate, char[] arAdditionalChars)
	{
		if (null == arAdditionalChars)
		{
			arAdditionalChars = new char[0];
		}
		// Build the range arrays appending the additional chars as
		// ranges of length one char to the end of the arrays.
		char[] arStartRange = new char[1 + arAdditionalChars.length];
		char[] arEndRange = new char[1 + arAdditionalChars.length];
		arStartRange[0] = '0';
		arEndRange[0] = '9';
		for (int i=0; i<arAdditionalChars.length; i++)
		{
			arStartRange[1 + i] = arAdditionalChars[i];
			arEndRange[1 + i] = arAdditionalChars[i];
		}
		return isComprisedOfChars(strCandidate, arStartRange, arEndRange);
	}

	public static boolean isComprisedOfChars(String strCandidate, char[] arStartRange, char[] arEndRange)
	{
		return isComprisedOfChars(strCandidate, arStartRange, arEndRange, null);
	}
	
	public static boolean isComprisedOfChars(String strCandidate, char[] arStartRange, char[] arEndRange, char[] arAdditionalChars)
	{
		if ((null == strCandidate) || (0 == strCandidate.length()))
		{	// If the candidate string has no chars, then it is AlphaNumeric plus or minus
			return true;
		}
		if ((null == arStartRange) || (null == arEndRange))
		{	// No character ranges defined, cannot be comprised of "nothing"
			return false;
		}
		if (arStartRange.length != arEndRange.length)
		{	// Start and End range arrays must be the same length
			return false;
		}
		StringBuilder sb = new StringBuilder(strCandidate);
		for (int i=0; i<sb.length(); i++)
		{
			char c = sb.charAt(i);
			boolean bCharInRange = false;
			for (int r=0; !bCharInRange && (r<arStartRange.length); r++)
			{
				if ((c >= arStartRange[r]) && (c <= arEndRange[r]))
				{	// This char is within this range
					bCharInRange = true;
				}
			}
			if (!bCharInRange)
			{	// Did not find a range containing this character!
				boolean bFoundAsAdditional = false;
				// Was it specified as an additional character?
				if (null != arAdditionalChars)
				{
					for (char cAdditional : arAdditionalChars)
					{
						if (cAdditional == c)
						{	// Yes, this "additional" character is "valid"
							bFoundAsAdditional = true;
						}
					}
				}
				if (!bFoundAsAdditional)
				{				
					return false;
				}
			}
		}
		return true;
	}

}
