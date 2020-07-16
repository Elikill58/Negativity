package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.inventory.SpigotInventory;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;

public class InventoryListeners implements Listener {
	
	@EventHandler
	public void onInventoryOpen(org.bukkit.event.inventory.InventoryOpenEvent e) {
		InventoryOpenEvent event = new InventoryOpenEvent(SpigotEntityManager.getPlayer((Player) e.getPlayer()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@EventHandler
	public void onInventoryClick(org.bukkit.event.inventory.InventoryClickEvent e) {
		if(!(e.getWhoClicked() instanceof Player) || e.getClickedInventory() == null || e.getCurrentItem() == null)
			return;
		com.elikill58.negativity.api.entity.Player p = SpigotEntityManager.getPlayer((Player) e.getWhoClicked());
		InventoryAction action = InventoryAction.valueOf(e.getAction().name());
		ItemStack item = new SpigotItemStack(e.getCurrentItem());
		InventoryClickEvent event = new InventoryClickEvent(p, action, e.getSlot(), item, new SpigotInventory(e.getClickedInventory()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		com.elikill58.negativity.api.entity.Player p = SpigotEntityManager.getPlayer((Player) e.getPlayer());
		EventManager.callEvent(new com.elikill58.negativity.api.events.inventory.InventoryCloseEvent(p, new SpigotInventory(e.getInventory())));
	}
}
