package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.spigot.inventories.holders.AlertHolder;
import com.elikill58.negativity.spigot.inventories.holders.CheckMenuHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.adapter.Adapter;

public class ActualizeInvTimer extends BukkitRunnable {

	private static final boolean INV_FREEZE_ACTIVE = Adapter.getAdapter().getConfig().getBoolean("inventory.inv_freeze_active");

	@Override
	public void run() {
		for (Player p : Inv.CHECKING.keySet()) {
			if (p.getOpenInventory() != null) {
				Inventory topInv = p.getOpenInventory().getTopInventory();
				if(topInv == null || !topInv.getType().equals(org.bukkit.event.inventory.InventoryType.CHEST)) {
					continue;
				}
				InventoryHolder holder = topInv.getHolder();
				if(!(holder instanceof NegativityHolder)) {
					continue;
				}
				NegativityHolder nh = (NegativityHolder) holder;
				if (nh instanceof CheckMenuHolder)
					AbstractInventory.getInventory(InventoryType.CHECK_MENU).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
					//CheckMenuInventory.actualizeCheckMenu(p, Inv.CHECKING.get(p));
				else if (nh instanceof AlertHolder)
					AbstractInventory.getInventory(InventoryType.ALERT).ifPresent((inv) -> inv.actualizeInventory(p, Inv.CHECKING.get(p)));
			} else
				Inv.CHECKING.remove(p);
		}
		for (Player p : Utils.getOnlinePlayers()) {
			if (NegativityPlayer.getCached(p.getUniqueId()).isFreeze && INV_FREEZE_ACTIVE) {
				AbstractInventory.open(InventoryType.FREEZE, p);
			}
		}
	}
}
