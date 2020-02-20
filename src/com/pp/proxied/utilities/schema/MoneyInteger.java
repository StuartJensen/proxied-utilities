package com.pp.proxied.utilities.schema;

import com.pp.proxied.utilities.util.StringUtil;

public class MoneyInteger
{
	private int m_iAmount;
	
	public static final MoneyInteger ZERO = new MoneyInteger(0);
	public static final MoneyInteger ONE_CENT = new MoneyInteger(1);
	
	public MoneyInteger(String strAmount)
		throws NumberFormatException
	{
		if (StringUtil.isDefined(strAmount))
		{
			if (StringUtil.isNumericPlus(strAmount, new char[]{'.'}))
			{
				int iDecimalPointIdx = strAmount.indexOf(".");
				if (-1 != iDecimalPointIdx)
				{	// Ensure there are two, and only two, integers following the decimal point.
					if (iDecimalPointIdx != (strAmount.length() - 3))
					{
						throw new NumberFormatException("Invalid number. Must have two decimal places: " + strAmount);
					}
					strAmount = StringUtil.replaceSubString(strAmount, ".", "");
					m_iAmount = Integer.parseInt(strAmount);
				}
				else
				{
					m_iAmount = Integer.parseInt(strAmount);
					m_iAmount *= 100;
				}
			}
			else
			{
				throw new NumberFormatException("Invalid number. Not numeric: " + strAmount);
			}
		}
	}
	
	public MoneyInteger(int iAmount)
	{
		m_iAmount = iAmount;
	}
	
	public int getAmount()
	{
		return m_iAmount;
	}
	
	public MoneyInteger plus(MoneyInteger that)
	{
		return new MoneyInteger(this.getAmount() + that.getAmount());
	}
	
	public MoneyInteger minus(MoneyInteger that)
	{
		return new MoneyInteger(this.getAmount() - that.getAmount());
	}
	
	public MoneyInteger divide(MoneyInteger that)
	{
		return new MoneyInteger(this.getAmount() / that.getAmount());
	}
	
	public MoneyInteger multiply(MoneyInteger that)
	{
		return new MoneyInteger(this.getAmount() * that.getAmount());
	}
	
	public String toString(int iIndent)
	{
		String strInDecimalFormat = (m_iAmount / 100) + ".";
		int rightSide = Math.abs(m_iAmount % 100);
		if (rightSide < 10)
		{
			strInDecimalFormat += "0";
		}
		strInDecimalFormat += rightSide;
		return strInDecimalFormat;
	}
	
	public String toString()
	{
		return toString(0);
	}
	
	@Override
	public int hashCode()
	{
		return 37 + (37 * getAmount());
	}
	
	@Override
	public boolean equals(Object that)
	{
		if (that instanceof MoneyInteger)
		{
			if (this == that)
			{	// Same instance
				return true;
			}
			if (this.getAmount() == ((MoneyInteger)that).getAmount())
			{
				return true;
			}
		}
		return false;
	}
	
}
