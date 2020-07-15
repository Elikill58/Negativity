package com.elikill58.negativity.common.timers;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.common.inventory.Inventory;
import com.elikill58.negativity.common.inventory.InventoryHolder;
import com.elikill58.negativity.common.inventory.InventoryManager;
import com.elikill58.negativity.common.inventory.InventoryType;
import com.elikill58.negativity.common.inventory.NegativityHolder;
import com.elikill58.negativity.inventories.holders.AlertHolder;
import com.elikill58.negativity.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ActualizeInvTimer implements Runnable {

	private static final boolean INV_FREEZE_ACTIVE = Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active");

	@Override
	public void run() {
		for (Player p : Inv.CHECKING.keySet()) {
			if (p.getOpenInventory() != null) {
				Inventory topInv = p.getOpenInventory();
				if(topInv == null || !topInv.getType().equals(InventoryType.CHEST)) {
					continue;
				}
				InventoryHolder holder = topInv.getHolder();
				if(!(holder instanceof NegativityHolder)) {
					continue;
				}
				NegativityHolder nh = (NegativityHolder) holder;
				if (nh instanceof CheckMenuHolder)
					InventoryManager.getInventory(NegativityInventory.CHECK_MENU).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
					//CheckMenuInventory.actualizeCheckMenu(p, Inv.CHECKING.get(p));
				else if (nh instanceof AlertHolder)
					InventoryManager.getInventory(NegativityInventory.ALERT).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
			} else
				Inv.CHECKING.remove(p);
		}
		for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
			if (NegativityPlayer.getCached(p.getUniqueId()).isFreeze && INV_FREEZE_ACTIVE) {
				InventoryManager.open(NegativityInventory.FREEZE, p);
			}
		}
	}
}
