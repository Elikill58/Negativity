package com.elikill58.negativity.api.timers;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.common.inventories.holders.AlertHolder;
import com.elikill58.negativity.common.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ActualizeInvTimer implements Runnable {

	private static final boolean INV_FREEZE_ACTIVE = Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active");

	@Override
	public void run() {
		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			if(!p.isOnline())
				continue;
			if (p.hasOpenInventory()) {
				PlatformHolder holder = p.getOpenInventory().getHolder();
				if(!(holder instanceof NegativityHolder)) {
					continue;
				}
				NegativityHolder nh = (NegativityHolder) holder;
				if (nh instanceof CheckMenuHolder)
					InventoryManager.getInventory(NegativityInventory.CHECK_MENU).ifPresent((inv) -> inv.actualizeInventory(p, ((CheckMenuHolder) nh).getCible()));
				else if (nh instanceof AlertHolder)
					InventoryManager.getInventory(NegativityInventory.ALERT).ifPresent((inv) -> inv.actualizeInventory(p, ((AlertHolder) nh).getCible()));
			}
		}
		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			if (NegativityPlayer.getNegativityPlayer(p).isFreeze && INV_FREEZE_ACTIVE) {
				InventoryManager.open(NegativityInventory.FREEZE, p);
			}
		}
	}
}
