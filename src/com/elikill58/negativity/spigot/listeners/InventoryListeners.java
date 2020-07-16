package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

public class InventoryListeners implements Listener {
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.inventory.InventoryOpenEvent(SpigotEntityManager.getPlayer((Player) e.getPlayer())));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null)
			return;
		com.elikill58.negativity.api.entity.Player p = SpigotEntityManager.getPlayer((Player) e.getWhoClicked());
		ItemStack item = e.getCurrentItem() == null ? null : new SpigotItemStack(e.getCurrentItem());
		EventManager.callEvent(new com.elikill58.negativity.api.events.inventory.InventoryClickEvent(p, InventoryAction.valueOf(e.getAction().name()), e.getSlot(), item, new SpigotInventory(e.getClickedInventory())));
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		com.elikill58.negativity.api.entity.Player p = SpigotEntityManager.getPlayer((Player) e.getPlayer());
		EventManager.callEvent(new com.elikill58.negativity.api.events.inventory.InventoryCloseEvent(p, new SpigotInventory(e.getInventory())));
	}
}
