package com.pp.proxied.utilities.util;

import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.TenantEntry;

public class PaymentDetails
{
	private TenantEntry m_tenantEntry;
	private int m_iPercentage;
	private MoneyInteger m_iAmount;
	private MoneyInteger m_iExtraPaidBeforeProcessing = MoneyInteger.ZERO;
	private MoneyInteger m_iExtraPaidDuringProcessing = MoneyInteger.ZERO;
	
	public PaymentDetails(TenantEntry tenantEntry)
	{
		m_tenantEntry = tenantEntry;
	}
	
	public PaymentDetails(TenantEntry tenantEntry, int iPercentage, MoneyInteger iAmount, MoneyInteger iExtraPaidBeforeProcessing)
	{
		m_tenantEntry = tenantEntry;
		m_iPercentage = iPercentage;
		m_iAmount = iAmount;
		if (null != iExtraPaidBeforeProcessing)
		{
			m_iExtraPaidBeforeProcessing = iExtraPaidBeforeProcessing;
		}
	}
	
	public void setPercentage(int iPercentage)
	{
		m_iPercentage = iPercentage;
	}
	
	public void setAmount(MoneyInteger amount)
	{
		m_iAmount = amount;
	}
	
	public TenantEntry getTenantEntry()
	{
		return m_tenantEntry;
	}
	
	public int getPercentage()
	{
		return m_iPercentage;
	}
	
	public int getAmount()
	{
		if (null != m_iAmount)
		{
			return m_iAmount.getAmount();
		}
		return 0;
	}
	
	public String getAmountDisplayName()
	{
		if (null != m_iAmount)
		{
			return m_iAmount.toString();
		}
		return "0";
	}
	
	public void setAmount(int iAmount)
	{
		m_iAmount = new MoneyInteger(iAmount);
	}
	
	public MoneyInteger getExtraPaidBeforeProcessing()
	{
		return m_iExtraPaidBeforeProcessing;
	}
	
	public void incExtraPaidDuringProcessing()
	{
		m_iExtraPaidDuringProcessing = m_iExtraPaidDuringProcessing.plus(MoneyInteger.ONE_CENT);
	}
	
	public MoneyInteger getExtraPaidDuringProcessing()
	{
		return m_iExtraPaidDuringProcessing;
	}
	
	public void setExtraPaidDuringProcessing(MoneyInteger value)
	{
		m_iExtraPaidDuringProcessing = value;
	}
}