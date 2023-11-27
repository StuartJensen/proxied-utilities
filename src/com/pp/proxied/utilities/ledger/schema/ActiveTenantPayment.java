package com.pp.proxied.utilities.ledger.schema;

import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.util.StringUtil;

public class ActiveTenantPayment
{
	private ActivePayment m_activePayment;
	private ActiveTenant m_activeTenant;
	private MoneyInteger m_iPaidAmount;
	private MoneyInteger m_iTotalPaidAmount;

	public ActiveTenantPayment(ActiveTenant activeTenant, ActivePayment activePayment)
	{
		m_activeTenant = activeTenant;
		m_activePayment = activePayment;
		m_iPaidAmount = MoneyInteger.ZERO;
		m_iTotalPaidAmount = MoneyInteger.ZERO;
	}
	
	public ActiveTenantPayment(ActiveTenantPayment source)
	{
		if (null != source)
		{
			m_activeTenant = new ActiveTenant(source.getActiveTenant());
			m_activePayment = new ActivePayment(source.getActivePayment());
			m_iPaidAmount = new MoneyInteger(source.getPaidAmount());
		}
	}
	
	public ActivePayment getActivePayment()
	{
		return m_activePayment;
	}
	
	public ActiveTenant getActiveTenant()
	{
		return m_activeTenant;
	}
	
	public void setPaidAmount(MoneyInteger iAmount)
	{
		m_iPaidAmount = iAmount;
	}
	
	public MoneyInteger getTotalPaidAmount()
	{
		return m_iTotalPaidAmount;
	}
	
	public void setTotalPaidAmount(MoneyInteger iAmount)
	{
		m_iTotalPaidAmount = iAmount;
	}
	
	public MoneyInteger addTotalPaidAmount(MoneyInteger iAmount)
	{
		m_iTotalPaidAmount = m_iTotalPaidAmount.plus(iAmount);
		return m_iTotalPaidAmount;
	}
	
	public void incPaidAmount()
	{
		m_iPaidAmount = m_iPaidAmount.plus(MoneyInteger.ONE_CENT);
	}
	
	public MoneyInteger getPaidAmount()
	{
		return m_iPaidAmount;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getSpaces(iIndent)).append("ActiveTenantPayment: ");
		sb.append("Paid: ").append(m_iPaidAmount.toString());
		sb.append(", Total Paid: ").append(m_iTotalPaidAmount.toString());
		sb.append(", Payee: ").append(getActivePayment().getPayment().getPayeeName());
		sb.append(", Tenant: ").append(getActiveTenant().getTenant().getTenantName());
		return sb.toString();
	}
}
