package com.pp.proxied.utilities;

import com.pp.proxied.utilities.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.schema.Entry;
import com.pp.proxied.utilities.schema.EntryFactory;
import com.pp.proxied.utilities.schema.TenantEntry;

public class EntryParser
{
	private List<String> m_lEntries;
	
	public EntryParser(File sourceFile)
		throws IOException
	{
		FileReader fileReader = new FileReader(sourceFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		m_lEntries = new ArrayList<String>();
		String strLine;
		while ((strLine = bufferedReader.readLine()) != null)
		{
			m_lEntries.add(strLine);
		}
		fileReader.close();
	}
	
	public List<Entry> parse()
		throws ParseException, InvalidParameterException
	{
		List<Entry> lResult = new ArrayList<Entry>();
		for (String strLine : m_lEntries)
		{
			strLine = strLine.trim();
			if (StringUtil.isDefined(strLine))
			{
				if (!strLine.startsWith("//"))
				{
					lResult.add(EntryFactory.getEntry(strLine));
				}
			}
		}
		
		Collections.sort(lResult);
		
		// Add the well-known, one and only, LANDLORD tenant.
		if (!lResult.isEmpty())
		{
			lResult.add(0, TenantEntry.createLandLordInstance(lResult.get(0).getDate()));
		}
		
		// Set all Nths
		int iNth = 0;
		for (Entry entry : lResult)
		{
			entry.setNth(iNth++);
		}

		return lResult;
	}
}
