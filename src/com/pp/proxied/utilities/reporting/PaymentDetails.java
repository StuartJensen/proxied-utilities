package com.pp.proxied.utilities.reporting;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.StringUtil;

public class PaymentDetails
{
	PaymentEntry m_paymentEntry;
	private Map<TenantEntry, AtomicInteger> mapDayCounts = new HashMap<TenantEntry, AtomicInteger>();
	private Map<TenantEntry, MoneyInteger> mapPaidAmount = new HashMap<TenantEntry, MoneyInteger>();

	public PaymentDetails(PaymentEntry paymentEntry)
	{
		m_paymentEntry = paymentEntry;
	}
	
	public void setPaidAmount(TenantEntry tenant, MoneyInteger amount)
	{
		mapPaidAmount.put(tenant, amount);
	}
	
	public int incDayCount(TenantEntry tenant)
	{
		AtomicInteger count = mapDayCounts.get(tenant);
		if (null == count)
		{
			count = new AtomicInteger(0);
			mapDayCounts.put(tenant, count);
		}
		return count.incrementAndGet();
	}
	
	public String report()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(m_paymentEntry.getDateString()).append(": Payee: ").append(m_paymentEntry.getPayeeName());
		sb.append(": ").append(m_paymentEntry.getAmount().toString());
		sb.append(", ").append(RegisterBaseEntry.toString(m_paymentEntry.getStartDate()));
		sb.append(" - ").append(RegisterBaseEntry.toString(m_paymentEntry.getEndDate()));
		sb.append("\n");
		
		int iTotalDays = m_paymentEntry.getDaysInPeriodInclusive();
		
		Iterator<TenantEntry> iter = mapDayCounts.keySet().iterator();
		MoneyInteger sumOfTenantPayments = MoneyInteger.ZERO;
		while (iter.hasNext())
		{
			TenantEntry key = iter.next();
			AtomicInteger dayValue = mapDayCounts.get(key);
			MoneyInteger paidValue = mapPaidAmount.get(key);
			// Calculate the percentage of days this tenant paid
			double dDayValue = (double)dayValue.get();
			double dTotalDays = (double)iTotalDays;
			double dDayPercentage = (dDayValue * 100) / dTotalDays;
			// Calculate the percentage of total amount this tenant paid
			double dPaidValue = (double)paidValue.getAmount();
			double dFullPaymentAmount = (double)m_paymentEntry.getAmount().getAmount();
			double dPaidPercentage = (dPaidValue * 100) / dFullPaymentAmount;

			sb.append(StringUtil.getSpaces(1));
			sb.append(key.getTenantName()).append(": Days: ").append(dayValue).append(", Paid: ").append(paidValue.toString());
			sb.append(", Days: ").append(new DecimalFormat("##.####").format(dDayPercentage)).append("%");
			sb.append(", Paid: ").append(new DecimalFormat("##.####").format(dPaidPercentage)).append("%");
			sb.append("\n");
			sumOfTenantPayments = sumOfTenantPayments.plus(paidValue);
		}
		
		String strBalanced = "Balanced";
		if (!sumOfTenantPayments.equals(m_paymentEntry.getAmount()))
		{
			strBalanced = "Mismatch";
		}
		sb.append(StringUtil.getSpaces(1));
		sb.append("[Sum: ").append(sumOfTenantPayments.toString()).append(": ").append(strBalanced).append("]\n");
		return sb.toString();
	}
}
