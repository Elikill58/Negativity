package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge.impl.item.SpongeItemStack;

public class InventoryListeners {
	
	@Listener
	public void onInventoryOpen(InteractInventoryEvent.Open e, @First Player p) {
		InventoryOpenEvent event = new InventoryOpenEvent(SpongeEntityManager.getPlayer(p));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@Listener
	@Exclude(ClickInventoryEvent.Double.class)
	public void onInvClick(ClickInventoryEvent e, @First Player p) {
		if(e.getTargetInventory() == null)
			return;
		InventoryAction action = InventoryAction.valueOf("");
		ItemStack item = new SpongeItemStack(e.getTransactions().get(0).getOriginal().createStack());
		InventoryClickEvent event = new InventoryClickEvent(SpongeEntityManager.getPlayer(p), action, -1, item, new SpongeInventory(e.getTargetInventory()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close e, @First Player p) {
		EventManager.callEvent(new InventoryCloseEvent(SpongeEntityManager.getPlayer(p), new SpongeInventory(e.getTargetInventory())));
	}
}
