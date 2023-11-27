package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.Ledger;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActivePayments;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayments;
import com.pp.proxied.utilities.ledger.schema.RegisterDeposits;
import com.pp.proxied.utilities.ledger.schema.RegisterPayments;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.StringUtil;

public class CashFlowReport
{
	private Ledger m_ledger;
	
	public CashFlowReport(Ledger ledger)
	{
		m_ledger = ledger;
	}
	
	public String toString(int iIndent)
	{
		List<CashFlowDetails> lDetails = new ArrayList<CashFlowDetails>();
		StringBuilder sb = new StringBuilder();
		List<LedgerEntry> lEntries = m_ledger.getLedgerEntries();
		if ((null == lEntries) || (lEntries.isEmpty()))
		{
			sb.append("Ledger is empty.");
		}
		else
		{	// Start at first entry's year
			int iYear = lEntries.get(0).getDate().get(Calendar.YEAR);
			CashFlowDetails details = new CashFlowDetails(iYear);
			for (LedgerEntry entry : lEntries)
			{	// Has the year changed?
				if (iYear != entry.getDate().get(Calendar.YEAR))
				{
					// Output this year's details to the report
					//details.complete();
					//sb.append(details.report());
					lDetails.add(details);
					// Reset data
					iYear = entry.getDate().get(Calendar.YEAR);
					details = new CashFlowDetails(iYear);
				}
				RegisterDeposits registerDeposits = entry.getRegisterDeposits();
				if ((null != registerDeposits) && (!registerDeposits.isEmpty()))
				{
					for (DepositEntry deposit : registerDeposits.getDeposits())
					{
						details.addTenantDeposit(deposit.getAssociatedTenantEntry(), deposit.getAmount());
					}
				}
				
				RegisterPayments registerPayments = entry.getRegisterPayments();
				if ((null != registerPayments) && (!registerPayments.isEmpty()))
				{
					for (PaymentEntry payment : registerPayments.getPayments())
					{
						details.addBilledPayeePayment(payment.getAssociatedPayeeEntry(), payment.getAmount());
					}
				}
				
				ActivePayments activePayments = entry.getActivePayments();
				if ((null != activePayments) && (!activePayments.isEmpty()))
				{
					for (ActivePayment activePayment : activePayments.getActivePayments())
					{
						PayeeEntry payeeEntry = activePayment.getPayment().getAssociatedPayeeEntry();
						ActiveTenantPayments activeTenantPayments = activePayment.getActiveTenantPayments();
						if ((null != activeTenantPayments) && (!activeTenantPayments.isEmpty()))
						{
							for (ActiveTenantPayment atp : activeTenantPayments.getActiveTenantPayments())
							{
								TenantEntry tenantEntry = atp.getActiveTenant().getTenant();
								details.addTenantPayment(tenantEntry, atp.getPaidAmount());
								details.addPayeePayment(payeeEntry, atp.getPaidAmount());
							}
						}
						
					}
				}
			}
			if (!details.isEmpty())
			{	// Report last year
				//details.complete();
				//sb.append(details.report());
				lDetails.add(details);
			}
			
			// Report sum for entire ledger
			MoneyInteger expenses = MoneyInteger.ZERO;
			MoneyInteger expensesBilled = MoneyInteger.ZERO;
			MoneyInteger income = MoneyInteger.ZERO;
			for (CashFlowDetails current : lDetails)
			{
				current.complete();
				expenses = expenses.plus(current.getExpenses());
				income = income.plus(current.getIncome());
				expensesBilled = expensesBilled.plus(current.getBilledExpenses());
			}
			sb.append("Total ledger income:  ").append(income.toString()).append("\n");
			sb.append("Total ledger expense: ").append(expenses.toString()).append("\n");
			sb.append("Total --------------: ").append(income.minus(expenses).toString()).append("\n");
			sb.append(StringUtil.getSpaces(1));
			sb.append("(Negative value means paid MORE than deposited)\n\n");
			sb.append("Total billed expense: ").append(expensesBilled.toString()).append("\n");
			sb.append(StringUtil.getSpaces(1));
			sb.append("(Should equal ledger expense)\n\n");
			for (CashFlowDetails current : lDetails)
			{
				sb.append(current.report());
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
