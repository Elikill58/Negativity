package com.elikill58.negativity.common.timers;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.common.inventories.holders.players.ActivedCheatHolder;
import com.elikill58.negativity.common.inventories.holders.players.AlertHolder;
import com.elikill58.negativity.common.inventories.holders.players.CheckMenuHolder;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.universal.Adapter;

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
					InventoryManager.getInventory(NegativityInventory.GLOBAL_PLAYER).ifPresent((inv) -> inv.actualizeInventory(p, ((CheckMenuHolder) nh).getCible()));
				else if (nh instanceof AlertHolder)
					InventoryManager.getInventory(NegativityInventory.ALERT).ifPresent((inv) -> inv.actualizeInventory(p, ((AlertHolder) nh).getCible()));
				else if (nh instanceof ActivedCheatHolder)
					InventoryManager.getInventory(NegativityInventory.ACTIVED_CHEAT).ifPresent((inv) -> inv.actualizeInventory(p, ((ActivedCheatHolder) nh).getCible()));
			}
		}
		if(INV_FREEZE_ACTIVE) {
			for(NegativityPlayer np : NegativityPlayer.getAllNegativityPlayers()) {
				if (np.isFreeze) {
					InventoryManager.open(NegativityInventory.FREEZE, np.getPlayer());
				}
			}
		}
	}
}
