package com.pp.proxied.utilities.reporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pp.proxied.utilities.ledger.InvalidLedgerException;
import com.pp.proxied.utilities.ledger.LedgerEntry;
import com.pp.proxied.utilities.ledger.LedgerEntryVisitor;
import com.pp.proxied.utilities.schema.BalanceEntry;
import com.pp.proxied.utilities.schema.DepositEntry;
import com.pp.proxied.utilities.schema.FlushEntry;
import com.pp.proxied.utilities.schema.MoneyInteger;
import com.pp.proxied.utilities.schema.PaymentEntry;
import com.pp.proxied.utilities.schema.RemoveEntry;
import com.pp.proxied.utilities.schema.TenantEntry;
import com.pp.proxied.utilities.schema.comparators.TenantEntryByTenantName;
import com.pp.proxied.utilities.util.PaymentDetails;

public class BalancesVisitor
	extends LedgerEntryVisitor
{
	private Map<TenantEntry, MoneyInteger> m_mapBalances;
	
	public BalancesVisitor()
	{
		m_mapBalances = new HashMap<TenantEntry, MoneyInteger>();
	}
	
	public Map<TenantEntry, MoneyInteger> getBalances()
	{
		return m_mapBalances;
	}
	
	public MoneyInteger getBalance(TenantEntry tenantEntry)
	{
		return m_mapBalances.get(tenantEntry);
	}
	
	public MoneyInteger getTotalBalance()
	{
		if ((null == m_mapBalances) || (m_mapBalances.isEmpty()))
		{
			return MoneyInteger.ZERO;
		}
		MoneyInteger total = MoneyInteger.ZERO;
		Collection<MoneyInteger> cMoney = m_mapBalances.values();
		if ((null != cMoney) && (!cMoney.isEmpty()))
		{
			for (MoneyInteger current : cMoney)
			{
				total = total.plus(current);
			}
		}
		return total;
	}
	
	@Override
	public LedgerEntryVisitor getInstance()
	{
		return new BalancesVisitor();
	}
	
	@Override
	public void preProcess()
		throws InvalidLedgerException
	{
	}
	
	@Override
	public void process()
		throws InvalidLedgerException
	{
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		
		if (null != getPreviousVisitor())
		{	// Pull balances from the previous visitor
			Map<TenantEntry, MoneyInteger> mPreviousBalances = ((BalancesVisitor)getPreviousVisitor()).getBalances();
			if (null != mPreviousBalances)
			{
				Iterator<TenantEntry> iter = mPreviousBalances.keySet().iterator();
				while (iter.hasNext())
				{
					TenantEntry tenantEntry = iter.next();
					MoneyInteger balance = mPreviousBalances.get(tenantEntry);
					m_mapBalances.put(tenantEntry, balance);
				}
			}
		}
		
		// Add any new Tenants
		List<TenantEntry> lTenants = currentLedgerEntry.getActiveTenants();
		if ((null != lTenants) && (!lTenants.isEmpty()))
		{
			for (TenantEntry tenantEntry : lTenants)
			{
				if (null == m_mapBalances.get(tenantEntry))
				{
					m_mapBalances.put(tenantEntry, MoneyInteger.ZERO);
				}
			}
		}
		
		// Process flush entries.
		List<FlushEntry> lFlushes = currentLedgerEntry.getActiveFlushes();
		if (null != lFlushes)
		{
			for (FlushEntry flushEntry : lFlushes)
			{
				MoneyInteger fromTenantBalance = m_mapBalances.get(flushEntry.getAssociatedFromTenantEntry());
				MoneyInteger toTenantBalance = m_mapBalances.get(flushEntry.getAssociatedToTenantEntry());
				if ((null != fromTenantBalance) && (null != toTenantBalance))
				{
					m_mapBalances.remove(flushEntry.getAssociatedFromTenantEntry());
					m_mapBalances.put(flushEntry.getAssociatedFromTenantEntry(), MoneyInteger.ZERO);
					toTenantBalance = toTenantBalance.plus(fromTenantBalance);
					m_mapBalances.remove(flushEntry.getAssociatedToTenantEntry());
					m_mapBalances.put(flushEntry.getAssociatedToTenantEntry(), toTenantBalance);
				}
			}
		}
		
		// Process balance entries.
		List<BalanceEntry> lBalances = currentLedgerEntry.getActiveBalances();
		if (null != lBalances)
		{
			for (BalanceEntry balanceEntry : lBalances)
			{
				m_mapBalances.remove(balanceEntry.getAssociatedTenantEntry());
				m_mapBalances.put(balanceEntry.getAssociatedTenantEntry(), balanceEntry.getBalance());
			}
		}

		List<DepositEntry> lDeposits = currentLedgerEntry.getActiveDeposits();
		if (null != lDeposits)
		{
			for (DepositEntry depositEntry : lDeposits)
			{
				MoneyInteger balance = m_mapBalances.get(depositEntry.getAssociatedTenantEntry());
				if (null == balance)
				{
					m_mapBalances.put(depositEntry.getAssociatedTenantEntry(), depositEntry.getAmount());
				}
				else
				{
					m_mapBalances.remove(depositEntry.getAssociatedTenantEntry());
					m_mapBalances.put(depositEntry.getAssociatedTenantEntry(), depositEntry.getAmount().plus(balance));
				}
			}
		}
		
		List<PaymentEntry> lPayments = currentLedgerEntry.getActivePayments();
		if (null != lPayments)
		{
			PaymentsVisitor paymentsVisitor = (PaymentsVisitor)currentLedgerEntry.getVisitor(PaymentsVisitor.class);
			for (PaymentEntry paymentEntry : lPayments)
			{
				List<PaymentDetails> lDetails = paymentsVisitor.getPaymentDetails(paymentEntry);
				if (null != lDetails)
				{
					for(PaymentDetails detail : lDetails)
					{
						applyPayment(detail.getTenantEntry(), new MoneyInteger(detail.getAmount()));
					}
				}
			}
		}
		
		// Process remove entries.
		List<RemoveEntry> lRemoves = currentLedgerEntry.getActiveRemoves();
		if (null != lRemoves)
		{
			for (RemoveEntry removeEntry : lRemoves)
			{
				if (removeEntry.getAssociatedTargetEntry() instanceof TenantEntry)
				{
					m_mapBalances.remove((TenantEntry)removeEntry.getAssociatedTargetEntry());
				}
			}
		}
	}
		
	@Override
	public void postProcess()
		throws InvalidLedgerException
	{	
	}
	
	private void applyPayment(TenantEntry tenantEntry, MoneyInteger iAmount)
	{
		MoneyInteger iExistingAmount = m_mapBalances.get(tenantEntry);
		if (null != iExistingAmount)
		{
			iAmount = iExistingAmount.minus(iAmount);
			m_mapBalances.remove(tenantEntry);
		}
		else
		{
			iAmount = MoneyInteger.ZERO.minus(iAmount);
		}
		m_mapBalances.put(tenantEntry, iAmount);
	}
	
	@Override
	public String toString(int iIndent)
	{
		StringBuilder sb = new StringBuilder();		
		LedgerEntry currentLedgerEntry = getCurrentLedgerEntry();
		sb.append(currentLedgerEntry.getDateString()).append(": ");
		sb.append("Balances: ");
		
		// Sort the tenants.
		List<TenantEntry> sortedTenants = new ArrayList<TenantEntry>(m_mapBalances.keySet());
		Collections.sort(sortedTenants, new TenantEntryByTenantName());
		MoneyInteger totalBalance = MoneyInteger.ZERO;
		StringBuilder sbTenants = new StringBuilder();
		for (TenantEntry tenantEntry : sortedTenants)
		{
			MoneyInteger iAmount = m_mapBalances.get(tenantEntry);
			sbTenants.append("[").append(tenantEntry.getTenantName()).append(": $").append(iAmount.toString()).append("] ");
			totalBalance = totalBalance.plus(iAmount);
		}

		// Show total running balance first
		sb.append("[Total: $").append(totalBalance.toString()).append("] ");
		sb.append(sbTenants);
		sb.append("\n");
		return sb.toString();
	}
	
	@Override
	public String toString()
	{
		return toString(0);
	}
}
