package com.pp.proxied.utilities.reporting;

import internal.atlaslite.jcce.util.StringUtil;

import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.schema.BalanceEntry;
import com.pp.proxied.utilities.schema.DepositEntry;
import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.PaymentEntry;

public class CashFlowReport
{
	private Ledger m_ledger;
	
	public CashFlowReport(Ledger ledger)
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
			sb.append(StringUtil.getSpaces(iIndent)).append("Cash Flow Report:\n");
			for (LedgerEntry entry : ledgerEntries)
			{
				StringBuilder sbTransactions = new StringBuilder();
				List<DepositEntry> lDeposits = entry.getActiveDeposits();
				if ((null != lDeposits) && (!lDeposits.isEmpty()))
				{
					for (DepositEntry depositEntry : lDeposits)
					{
						sbTransactions.append(StringUtil.getSpaces(iIndent+2)).append("Deposit: From: ").append(depositEntry.getTenantName()).append(", $").append(depositEntry.getAmount().toString()).append("\n");
					}
				}
				List<PaymentEntry> lPayments = entry.getActivePayments();
				if ((null != lPayments) && (!lPayments.isEmpty()))
				{
					for (PaymentEntry paymentEntry : lPayments)
					{
						sbTransactions.append(StringUtil.getSpaces(iIndent+2)).append("Payment: To: ").append(paymentEntry.getPayeeName()).append(", $").append(paymentEntry.getAmount().toString()).append("\n");
					}
				}
				List<BalanceEntry> lBalances = entry.getActiveBalances();
				if ((null != lBalances) && (!lBalances.isEmpty()))
				{
					for (BalanceEntry balanceEntry : lBalances)
					{
						sbTransactions.append(StringUtil.getSpaces(iIndent+2)).append("Balance Set: For: ").append(balanceEntry.getTenantName()).append(", $").append(balanceEntry.getBalance().toString().toString()).append("\n");
					}
				}
				if (0 != sbTransactions.length())
				{
					sb.append(StringUtil.getSpaces(iIndent+1)).append(entry.getDateString()).append(": ");
					BalancesVisitor balancesVisitor = (BalancesVisitor)entry.getVisitor(BalancesVisitor.class);
					if (null != balancesVisitor)
					{
						MoneyInteger total = balancesVisitor.getTotalBalance();
						sb.append("Running Balance: ").append(", $").append(total.toString().toString()).append("\n");
					}
					sb.append(sbTransactions);
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
