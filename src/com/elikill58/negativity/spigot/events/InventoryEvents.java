package com.elikill58.negativity.spigot.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.utils.Utils;

public class InventoryEvents implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getCurrentItem() == null || e.getClickedInventory() == null || !(e.getWhoClicked() instanceof Player))
			return;
		if (Utils.getInventoryName(e).equals(Inv.NAME_FREEZE_MENU)) {
			e.setCancelled(true);
		}
	}
}
