package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
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
	public void onInvClick(ClickInventoryEvent e, @First Player p) {
		if(e.getTargetInventory() == null || e.getTransactions().size() == 0)
			return;
		InventoryAction action = getAction(e);
		ItemStack item = new SpongeItemStack(e.getTransactions().get(0).getOriginal().createStack());
		InventoryClickEvent event = new InventoryClickEvent(SpongeEntityManager.getPlayer(p), action, -1, item, new SpongeInventory(e.getTargetInventory()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	private InventoryAction getAction(ClickInventoryEvent e) {
		if(e instanceof ClickInventoryEvent.Double)
			return InventoryAction.DOUBLE;
		else if(e instanceof ClickInventoryEvent.Creative)
			return InventoryAction.CREATIVE;
		else if(e instanceof ClickInventoryEvent.Drop)
			return InventoryAction.DROP;
		else if(e instanceof ClickInventoryEvent.Middle)
			return InventoryAction.MIDDLE;
		else if(e instanceof ClickInventoryEvent.Shift)
			return InventoryAction.LEFT_SHIFT;
		else if(e instanceof ClickInventoryEvent.NumberPress)
			return InventoryAction.NUMBER;
		return InventoryAction.UNKNOWN;
	}
	
	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close e, @First Player p) {
		EventManager.callEvent(new InventoryCloseEvent(SpongeEntityManager.getPlayer(p), new SpongeInventory(e.getTargetInventory())));
	}
}
