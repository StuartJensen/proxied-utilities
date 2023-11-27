package com.pp.proxied.utilities.ledger;

import java.util.List;

import com.pp.proxied.utilities.extra.paid.ExtraPaidManager;
import com.pp.proxied.utilities.ledger.schema.ActiveTenant;
import com.pp.proxied.utilities.ledger.schema.ActiveTenants;
import com.pp.proxied.utilities.ledger.schema.PassiveTenants;
import com.pp.proxied.utilities.ledger.schema.RegisterMoveOuts;
import com.pp.proxied.utilities.ledger.schema.RegisterRemoves;
import com.pp.proxied.utilities.ledger.schema.RegisterTenants;
import com.pp.proxied.utilities.register.schema.MoveOutEntry;
import com.pp.proxied.utilities.register.schema.RemoveEntry;
import com.pp.proxied.utilities.register.schema.TenantEntry;

public class ActiveTenantProcessor
{
	public void process(Ledger ledger)
	{
		List<LedgerEntry> lLedgerEntries = ledger.getLedgerEntries();
		for (LedgerEntry current : lLedgerEntries)
		{
			ActiveTenants activeTenants = new ActiveTenants();
			PassiveTenants passiveTenants = new PassiveTenants();
			LedgerEntry previous = current.getPreviousEntry();
			if (null != previous)
			{	// If not the first entry, clone the active an passive
				// tenants from the previous entry.
				activeTenants = new ActiveTenants(previous.getActiveTenants());
				passiveTenants = new PassiveTenants(previous.getPassiveTenants());
			}

			// Process TENANT entries on the current ledger entry
			RegisterTenants registerTenants = current.getRegisterTenants();
			if ((null != registerTenants) && (!registerTenants.isEmpty()))
			{
				for (TenantEntry tenantEntry : registerTenants.getTenants())
				{
					ExtraPaidManager.getInstance().add(tenantEntry);
					if (!tenantEntry.isLandlordInstance())
					{	// Do not count the landlord as an active tenant.
						activeTenants.add(new ActiveTenant(tenantEntry));
					}
				}
			}
			// Process MOVEOUT entries while the current ledger entry is
			// the "next" NEXT ledger entry. Process MOVEOUT entries the day
			// after they are entered in the ledger so that the tenant moves
			// out at midnight of the day of the current ledger entry. On
			// the move in side, tenants move in at midnight of the day
			// where the ledger entry resides.
			if (null != previous)
			{
				RegisterMoveOuts registerMoveOuts = previous.getRegisterMoveOuts();
				if ((null != registerMoveOuts) && (!registerMoveOuts.isEmpty()))
				{
					List<MoveOutEntry> lMoveOutEntries = registerMoveOuts.getMoveOuts();
					for (MoveOutEntry moveOut : lMoveOutEntries)
					{
						activeTenants.remove(moveOut);
						passiveTenants.add(moveOut);
					}
				}
			}
			// Process REMOVE entries on the current ledger entry
			RegisterRemoves registerRemoves = current.getRegisterRemoves();
			if ((null != registerRemoves) && (!registerRemoves.isEmpty()))
			{
				List<RemoveEntry> lRemoveEntries = registerRemoves.getRemoves();
				for (RemoveEntry remove : lRemoveEntries)
				{
					passiveTenants.remove(remove);
				}
			}
			
			current.setActiveTenants(activeTenants);
			current.setPassiveTenants(passiveTenants);
		}

	}
}
