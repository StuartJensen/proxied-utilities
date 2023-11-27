package com.pp.proxied.utilities.ledger;

import java.util.List;
import java.util.Set;

import com.pp.proxied.utilities.extra.paid.ExtraPaidManager;
import com.pp.proxied.utilities.ledger.schema.ActivePayment;
import com.pp.proxied.utilities.ledger.schema.ActiveTenant;
import com.pp.proxied.utilities.ledger.schema.ActiveTenantPayment;
import com.pp.proxied.utilities.ledger.schema.PassiveTenant;
import com.pp.proxied.utilities.register.schema.BalanceEntry;
import com.pp.proxied.utilities.register.schema.DepositEntry;
import com.pp.proxied.utilities.register.schema.MoneyInteger;
import com.pp.proxied.utilities.register.schema.TenantEntry;
import com.pp.proxied.utilities.util.GenericDouble;

public class BalanceProcessor
{
	public void process(Ledger ledger)
	{
		Set<TenantEntry> setTenants = ExtraPaidManager.getInstance().getTenants();
		for (TenantEntry tenant : setTenants)
		{
			MoneyInteger balance = MoneyInteger.ZERO;
			List<LedgerEntry> lLedgerEntries = ledger.getLedgerEntries();
			for (LedgerEntry current : lLedgerEntries)
			{
				BalanceEntry balanceEntry = current. getRegisterBalance(tenant);
				if (null != balanceEntry)
				{
					balance = balanceEntry.getBalance();
				}
				
				ActiveTenant activeTenant = current.getActiveTenant(tenant);
				if (null != activeTenant)
				{
					DepositEntry deposit = current.getRegisterDeposit(tenant);
					if (null != deposit)
					{
						balance = balance.plus(deposit.getAmount());
					}

					List<GenericDouble<ActivePayment, ActiveTenantPayment>> lPayments = current.getActiveTenantPayments(tenant);
					for (GenericDouble<ActivePayment, ActiveTenantPayment> payment : lPayments)
					{
						balance = balance.minus(payment.second.getPaidAmount());
					}
					activeTenant.setBalance(balance);
				}
				else
				{
					PassiveTenant passiveTenant = current.getPassiveTenant(tenant);
					if (null != passiveTenant)
					{
						DepositEntry deposit = current.getRegisterDeposit(tenant);
						if (null != deposit)
						{
							balance = balance.plus(deposit.getAmount());
						}
						passiveTenant.setBalance(balance);
					}
				}
			}
		}
	}

}
