package com.pp.proxied.utilities.ledger.schema;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.StringUtil;

public class ActivePayment
{
	private ActivePayment m_previous;
	private PaymentEntry m_paymentEntry;
	private int m_iTotalDaysInPeriod;
	private ActiveTenantPayments m_activeTenantPayments;
	
	private MoneyInteger m_iRemainingAmount;
	private int m_iRemainingDaysInPeriod;

	public ActivePayment(Calendar paymentDate, PaymentEntry paymentEntry)
	{
		m_paymentEntry = paymentEntry;
		m_iTotalDaysInPeriod = paymentEntry.getDaysInPeriodInclusive();
		m_iRemainingAmount = paymentEntry.getAmount();
		m_iRemainingDaysInPeriod = m_iTotalDaysInPeriod;
		if (!paymentEntry.inServiceOn(paymentDate))
		{
			throw new IllegalStateException("ERROR: Active payment being set on a date outside of the payment date range.");
		}
	}
	
	public ActivePayment(ActivePayment source)
	{
		if (null != source)
		{
			m_paymentEntry = source.getPayment();
			m_iTotalDaysInPeriod = source.getTotalDaysInPeriod();
			if (null != source.getActiveTenantPayments())
			{
				m_activeTenantPayments = new ActiveTenantPayments(source.getActiveTenantPayments());
			}
		}
	}
	
	public void setPrevious(ActivePayment previous)
	{
		m_previous = previous;
	}
	
	public PaymentEntry getPayment()
	{
		return m_paymentEntry;
	}
	
	public ActiveTenantPayments getActiveTenantPayments()
	{
		return m_activeTenantPayments;
	}
	
	public void setActiveTenantPayments(ActiveTenantPayments activeTenantPayments)
	{
		m_activeTenantPayments = activeTenantPayments;
	}
	
	public int getActiveTenantPaymentsSize()
	{
		if (null != getActiveTenantPayments())
		{
			return getActiveTenantPayments().size();
		}
		return 0;
	}
	
	public ActiveTenantPayment getActiveTenantPayment(TenantEntry target)
	{
		if ((null != getActiveTenantPayments()) && (!getActiveTenantPayments().isEmpty()))
		{
			for (ActiveTenantPayment activeTenantPayment : getActiveTenantPayments().getActiveTenantPayments())
			{
				if (target.equals(activeTenantPayment.getActiveTenant().getTenant()))
				{
					return activeTenantPayment;
				}
			}
		}
		return null;
	}
	
	public List<ActiveTenantPayment> getActiveTenantPayments(PayeeEntry target)
	{
		if ((null != getActiveTenantPayments()) && (!getActiveTenantPayments().isEmpty()))
		{
			if (getPayment().getPayeeName().equals(target.getPayeeName()))
			{
				return getActiveTenantPayments().getActiveTenantPayments();
			}
		}
		return Collections.emptyList();
	}
	
	public void setRemainingAmount(MoneyInteger iRemainingAmount)
	{
		m_iRemainingAmount = iRemainingAmount;
	}
	
	public MoneyInteger decRemainingAmount(MoneyInteger iToMinus)
	{
		m_iRemainingAmount = m_iRemainingAmount.minus(iToMinus);
		return m_iRemainingAmount;
	}
	
	public MoneyInteger decRemainingAmount(int iToMinus)
	{
		m_iRemainingAmount = m_iRemainingAmount.minus(iToMinus);
		return m_iRemainingAmount;
	}
	
	public MoneyInteger getRemainingAmount()
	{
		return m_iRemainingAmount;
	}
	
	public int decRemainingDaysInPeriod()
	{
		m_iRemainingDaysInPeriod--;
		return m_iRemainingDaysInPeriod;
	}
	
	public void setRemainingDaysInPeriod(int iRemainingDaysInPeriod)
	{
		m_iRemainingDaysInPeriod = iRemainingDaysInPeriod;
	}
	
	public int getRemainingDaysInPeriod()
	{
		return m_iRemainingDaysInPeriod;
	}
	
	public void calculate()
	{
		if (null != m_previous)
		{
			setRemainingAmount(m_previous.getRemainingAmount());
			setRemainingDaysInPeriod(m_previous.getRemainingDaysInPeriod());
			// Bring active tenant payment data down from previous
			getActiveTenantPayments().updateFromPrevious(m_previous.getActiveTenantPayments());
		}
		
		MoneyInteger fullAmount = getRemainingAmount();
		MoneyInteger dailyAmount = fullAmount.divide(m_iRemainingDaysInPeriod);
		
		int iTenantCount = getActiveTenantPaymentsSize();
		if (0 == iTenantCount)
		{
			throw new IllegalStateException("ERROR: Zero Tenants. Unable to calculate tenant payments.");
		}
		
		MoneyInteger dailyPerTenant = dailyAmount.divide(iTenantCount);
		MoneyInteger iRemainder = fullAmount.minus(dailyPerTenant.multiply(iTenantCount).multiply(m_iRemainingDaysInPeriod));
		
		for (ActiveTenantPayment activeTenantPayment : getActiveTenantPayments().getActiveTenantPayments())
		{
			activeTenantPayment.setPaidAmount(dailyPerTenant);
		}
		
		MoneyInteger iRemaindersToSpend = MoneyInteger.ZERO;
		if (!MoneyInteger.ZERO.equals(iRemainder))
		{
			iRemaindersToSpend = iRemainder;
			if (1 != getRemainingDaysInPeriod())
			{	// For last day already set do all remaining remainders above
				iRemaindersToSpend = iRemainder.divide(getRemainingDaysInPeriod());
				if (MoneyInteger.ZERO.equals(iRemaindersToSpend))
				{
					iRemaindersToSpend = MoneyInteger.ONE_CENT;
				}
			}
			if (!MoneyInteger.ZERO.equals(iRemaindersToSpend))
			{	// Add remainders to daily payment
				getActiveTenantPayments().spendExtraCents(iRemaindersToSpend);
			}
		}
		decRemainingAmount(dailyPerTenant.multiply(iTenantCount).plus(iRemaindersToSpend));
		decRemainingDaysInPeriod();
		
		for (ActiveTenantPayment activeTenantPayment : getActiveTenantPayments().getActiveTenantPayments())
		{
			activeTenantPayment.addTotalPaidAmount(activeTenantPayment.getPaidAmount());
		}
		
		if (0 == getRemainingDaysInPeriod())
		{	// Do some sanity checking
			if (!MoneyInteger.ZERO.equals(getRemainingAmount()))
			{
				StringBuilder sb = new StringBuilder("Payment remaining amount is not zero at end of service period.\n");
				sb.append(toString(1));
				throw new IllegalStateException(sb.toString());
			}
		}
	}
	
	public int getTotalDaysInPeriod()
	{
		return m_iTotalDaysInPeriod;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtil.getSpaces(iIndent)).append("ActivePayment: ");
		sb.append("Total Amount: ").append(m_paymentEntry.getAmount().toString());
		sb.append(", Remaining Amount: ").append(m_iRemainingAmount.toString());
		sb.append(", Total Days: ").append(getTotalDaysInPeriod());
		sb.append(", Remaining Days: ").append(m_iRemainingDaysInPeriod);
		sb.append(", Payee: ").append(getPayment().getAssociatedPayeeEntry().getPayeeName());
		if (null != getActiveTenantPayments())
		{
			sb.append("\n");
			sb.append(getActiveTenantPayments().toString(iIndent + 1));
		}
		return sb.toString();
	}
}
