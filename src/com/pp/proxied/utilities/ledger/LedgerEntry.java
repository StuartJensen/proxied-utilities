package com.pp.proxied.utilities.ledger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActivePayments;
import com.pp.proxied.utilities.ledger.schema.ActiveTenant;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenants;
import com.pp.proxied.utilities.ledger.schema.PassiveTenant;
import com.pp.proxied.utilities.ledger.schema.PassiveTenants;
import com.pp.proxied.utilities.ledger.schema.RegisterBalances;
import com.pp.proxied.utilities.ledger.schema.RegisterDeposits;
import com.pp.proxied.utilities.ledger.schema.RegisterFlushes;
import com.pp.proxied.utilities.ledger.schema.RegisterMoveOuts;
import com.pp.proxied.utilities.ledger.schema.RegisterPayees;
import com.pp.proxied.utilities.ledger.schema.RegisterPayments;
import com.pp.proxied.utilities.ledger.schema.RegisterRemoves;
import com.pp.proxied.utilities.ledger.schema.RegisterTenants;
import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.FlushEntry;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.PayeeEntry;
import com.pp.proxied.utilities.register.schema.PaymentEntry;
import com.pp.proxied.utilities.register.schema.RegisterBaseEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.DateUtil;
import com.pp.proxied.utilities.util.GenericDouble;

public class LedgerEntry
{
	private int m_iNth;
	private Calendar m_date;
	
	private RegisterTenants m_registerTenants;
	private RegisterPayees m_registerPayees;
	private RegisterBalances m_registerBalances;
	private RegisterPayments m_registerPayments;
	private RegisterDeposits m_registerDeposits;
	private RegisterMoveOuts m_registerMoveOuts;
	private RegisterRemoves m_registerRemoves;
	private RegisterFlushes m_registerFlushes;
	
	private LedgerEntry m_previousEntry;
	private LedgerEntry m_nextEntry;
	
	private ActiveTenants m_activeTenants;
	private PassiveTenants m_passiveTenants;
	private ActivePayments m_activePayments;
	
	public LedgerEntry(int iNthEntryInLedger, Calendar date)
	{
		m_iNth = iNthEntryInLedger;
		m_date = date;
	}
	
	public Calendar getDate()
	{
		Calendar result = Calendar.getInstance();
		result.setTimeInMillis(m_date.getTimeInMillis());
		return result;
	}
	
	public String getDateString()
	{
		return DateUtil.getTime(RegisterBaseEntry.STANDARD_DATEFORMAT, m_date.getTimeInMillis());
	}
	
	public int getNth()
	{
		return m_iNth;
	}
	
	public void setPreviousEntry(LedgerEntry previousEntry)
	{
		m_previousEntry = previousEntry;
	}
	
	public LedgerEntry getPreviousEntry()
	{
		return m_previousEntry;
	}
	
	public void setNextEntry(LedgerEntry nextEntry)
	{
		m_nextEntry = nextEntry;
	}
	
	public LedgerEntry getNextEntry()
	{
		return m_nextEntry;
	}
	
	public boolean isBefore(Calendar that)
	{
		return (0 > RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOnOrBefore(Calendar that)
	{
		return (0 >= RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isAfter(Calendar that)
	{
		return (0 < RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOnOrAfter(Calendar that)
	{
		return (0 <= RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public boolean isOn(Calendar that)
	{
		return (0 == RegisterBaseEntry.compareMonthDayYear(getDate(), that));
	}
	
	public void addTenant(TenantEntry tenant)
	{
		if (null == m_registerTenants)
		{
			m_registerTenants = new RegisterTenants();
		}
		m_registerTenants.add(tenant);
	}
	
	public RegisterTenants getRegisterTenants()
	{
		return m_registerTenants;
	}

	public void addMoveOut(MoveOutEntry moveOut)
	{
		if (null == m_registerMoveOuts)
		{
			m_registerMoveOuts = new RegisterMoveOuts();
		}
		m_registerMoveOuts.add(moveOut);
	}
	
	public RegisterMoveOuts getRegisterMoveOuts()
	{
		return m_registerMoveOuts;
	}
	
	public void addPayee(PayeeEntry payee)
	{
		if (null == m_registerPayees)
		{
			m_registerPayees = new RegisterPayees();
		}
		m_registerPayees.add(payee);
	}
	
	public RegisterPayees getRegisterPayees()
	{
		return m_registerPayees;
	}
	
	public void addBalance(BalanceEntry balance)
	{
		if (null == m_registerBalances)
		{
			m_registerBalances = new RegisterBalances();
		}
		m_registerBalances.add(balance);
	}
	
	public RegisterBalances getRegisterBalances()
	{
		return m_registerBalances;
	}
	
	public BalanceEntry getRegisterBalance(TenantEntry target)
	{
		if ((null != m_registerBalances) && (!m_registerBalances.isEmpty()))
		{
			return m_registerBalances.getRegisterBalance(target);
		}
		return null;
	}
	
	public void addPayment(PaymentEntry payment)
	{
		if (null == m_registerPayments)
		{
			m_registerPayments = new RegisterPayments();
		}
		m_registerPayments.add(payment);
	}
	
	public RegisterPayments getRegisterPayments()
	{
		return m_registerPayments;
	}

	public void addDeposit(DepositEntry deposit)
	{
		if (null == m_registerDeposits)
		{
			m_registerDeposits = new RegisterDeposits();
		}
		m_registerDeposits.add(deposit);
	}
	
	public RegisterDeposits getRegisterDeposits()
	{
		return m_registerDeposits;
	}
	
	public DepositEntry getRegisterDeposit(TenantEntry target)
	{
		if ((null != getRegisterDeposits()) && (!getRegisterDeposits().isEmpty()))
		{
			return getRegisterDeposits().getDeposit(target);
		}
		return null;
	}
	
	public void addRemove(RemoveEntry remove)
	{
		if (null == m_registerRemoves)
		{
			m_registerRemoves = new RegisterRemoves();
		}
		m_registerRemoves.add(remove);
	}
	
	public RegisterRemoves getRegisterRemoves()
	{
		return m_registerRemoves;
	}
	
	public RemoveEntry getRegisterRemove(TenantEntry target)
	{
		if ((null != getRegisterRemoves()) && (!getRegisterRemoves().isEmpty()))
		{
			return getRegisterRemoves().getRemove(target);
		}
		return null;
	}
	
	public void addFlush(FlushEntry flush)
	{
		if (null == m_registerFlushes)
		{
			m_registerFlushes = new RegisterFlushes();
		}
		m_registerFlushes.add(flush);
	}
	
	public RegisterFlushes getRegisterFlushes()
	{
		return m_registerFlushes;
	}
	
	public void addActiveTenant(ActiveTenant tenant)
	{
		if (null == m_activeTenants)
		{
			m_activeTenants = new ActiveTenants();
		}
		m_activeTenants.add(tenant);
	}
	
	public ActiveTenants getActiveTenants()
	{
		return m_activeTenants;
	}
	
	public ActiveTenant getActiveTenant(TenantEntry target)
	{
		if (null != getActiveTenants())
		{
			return getActiveTenants().getActiveTenant(target);
		}
		return null;
	}
	
	public void setActiveTenants(ActiveTenants activeTenants)
	{
		m_activeTenants = activeTenants;
	}

	public void addPassiveTenant(PassiveTenant tenant)
	{
		if (null == m_passiveTenants)
		{
			m_passiveTenants = new PassiveTenants();
		}
		m_passiveTenants.add(tenant);
	}
	
	public PassiveTenants getPassiveTenants()
	{
		return m_passiveTenants;
	}
	
	public PassiveTenant getPassiveTenant(TenantEntry target)
	{
		if (null != getPassiveTenants())
		{
			return getPassiveTenants().getPassiveTenant(target);
		}
		return null;
	}
	
	public void setPassiveTenants(PassiveTenants passiveTenants)
	{
		m_passiveTenants = passiveTenants;
	}

	public void addActivePayment(ActivePayment tenant)
	{
		if (null == m_activePayments)
		{
			m_activePayments = new ActivePayments();
		}
		m_activePayments.add(tenant);
	}
	
	public ActivePayments getActivePayments()
	{
		return m_activePayments;
	}
	
	public ActivePayment getActivePayment(PaymentEntry target)
	{
		if ((null != getActivePayments()) && (!getActivePayments().isEmpty()))
		{
			for (ActivePayment activePayment : getActivePayments().getActivePayments())
			{
				if (activePayment.getPayment().equals(target))
				{
					return activePayment;
				}
			}
		}
		return null;
	}
	
	public List<GenericDouble<ActivePayment, ActiveTenantPayment>> getActiveTenantPayments(TenantEntry target)
	{
		List<GenericDouble<ActivePayment, ActiveTenantPayment>> lResults = new ArrayList<GenericDouble<ActivePayment, ActiveTenantPayment>>();
		if ((null != getActivePayments()) && (!getActivePayments().isEmpty()))
		{
			for (ActivePayment activePayment : getActivePayments().getActivePayments())
			{
				ActiveTenantPayment activeTenantPayment = activePayment.getActiveTenantPayment(target);
				if (null != activeTenantPayment)
				{
					lResults.add(new GenericDouble<ActivePayment, ActiveTenantPayment>(activePayment, activeTenantPayment));
				}
			}
		}
		return lResults;
	}
	
	public List<ActiveTenantPayment> getActiveTenantPayments(PayeeEntry payee)
	{
		List<ActiveTenantPayment> lResults = new ArrayList<ActiveTenantPayment>();
		if ((null != getActivePayments()) && (!getActivePayments().isEmpty()))
		{
			lResults.addAll(getActivePayments().getActiveTenantPayments(payee));
		}
		return lResults;
	}
	
	public void setActivePayments(ActivePayments activePayments)
	{
		m_activePayments = activePayments;
	}
}
