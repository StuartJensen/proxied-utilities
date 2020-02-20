package com.pp.proxied.utilities.reporting;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.TenantEntry;
import com.pp.proxied.utilities.util.PaymentDetails;

import internal.atlaslite.jcce.util.StringUtil;

public class ExtraPaidReport
{
	private Ledger m_ledger;
	
	public ExtraPaidReport(Ledger ledger)
	{
		m_ledger = ledger;
	}
	
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();
		List<LedgerEntry> ledgerEntries = m_ledger.getLedgerEntries();
		if ((null == ledgerEntries) || (ledgerEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		else
		{
			sb.append(StringUtil.getSpaces(iIndent)).append("Extra Paid Report:\n");
			for (LedgerEntry entry : ledgerEntries)
			{
				List<PaymentEntry> lPayments = entry.getActivePayments();
				if ((null != lPayments) && (!lPayments.isEmpty()))
				{
					PaymentsVisitor paymentsVisitor = (PaymentsVisitor)entry.getVisitor(PaymentsVisitor.class);
					if (null != paymentsVisitor)
					{
						boolean bNonZeroDuring = false;
						sb.append(StringUtil.getSpaces(iIndent)).append(entry.getDateString()).append(":\n");
						Map<PaymentEntry, List<PaymentDetails>> mapPayments = paymentsVisitor.getPayments();
						Iterator<PaymentEntry> iter = mapPayments.keySet().iterator();
						while (iter.hasNext())
						{
							PaymentEntry paymentEntry = iter.next();
							sb.append(StringUtil.getSpaces(iIndent+1)).append(paymentEntry.getPayeeName());
							sb.append(", Total: $").append(paymentEntry.getAmount().toString()).append("\n");
							List<PaymentDetails> lDetails = mapPayments.get(paymentEntry);
							if (null != lDetails)
							{
								for (PaymentDetails details : lDetails)
								{
									MoneyInteger during = details.getExtraPaidDuringProcessing();
									if (0 != during.getAmount())
									{
										bNonZeroDuring = true;
									}
									sb.append(StringUtil.getSpaces(iIndent + 2)).append(details.getTenantEntry().getTenantName());
									sb.append(", Paid: $").append(details.getAmountDisplayName());
									sb.append(", ").append(details.getPercentage()).append("%");
									sb.append(", Extra During: $").append(during.toString());
									sb.append("\n");
								}
							}
						}
						// Now show totals after all payments are done
						if (bNonZeroDuring)
						{	// But only if there was some change in totals
							sb.append(StringUtil.getSpaces(iIndent+1)).append("Totals:\n");
							Map<TenantEntry, MoneyInteger> mapBefore = paymentsVisitor.getExtraPaidBeforeProcessing();
							Map<TenantEntry, MoneyInteger> mapDuring = paymentsVisitor.getExtraPaidDuringProcessing();
							
							Iterator<TenantEntry> iterBefore = mapBefore.keySet().iterator();
							while (iterBefore.hasNext())
							{
								TenantEntry tenantEntry = iterBefore.next();
								MoneyInteger before = mapBefore.get(tenantEntry);
								MoneyInteger during = mapDuring.get(tenantEntry);
								MoneyInteger total = before;
								if (null != during)
								{
									total = total.plus(during);
								}
								sb.append(StringUtil.getSpaces(iIndent + 2)).append(tenantEntry.getTenantName());
								sb.append(", Before: $").append(before.toString());
								sb.append(", During: ").append(((null != during) ? "$" + during.toString() : "$0.00"));
								sb.append(", Total: $").append(total.toString());
								sb.append("\n");
							}
						}
					}
				}
			}
		}
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}

}
