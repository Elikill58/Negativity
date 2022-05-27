package com.elikill58.negativity.sponge9.listeners;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge9.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge9.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge9.impl.item.SpongeItemStack;

public class InventoryListeners {
	
	@Listener
	public void onInventoryOpen(ClickContainerEvent.Open e, @First ServerPlayer p) {
		InventoryOpenEvent event = new InventoryOpenEvent(SpongeEntityManager.getPlayer(p));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@Listener
	public void onInvClick(ClickContainerEvent e, @First ServerPlayer p) {
		if (e.transactions().isEmpty()) {
			return;
		}
		
		InventoryAction action = getAction(e);
		SlotTransaction transaction = e.transactions().get(0);
		ItemStack item = new SpongeItemStack(transaction.original().createStack());
		int slotIndex = transaction.slot().getInt(Keys.SLOT_INDEX).orElse(-1);
		InventoryClickEvent event = new InventoryClickEvent(SpongeEntityManager.getPlayer(p), action, slotIndex, item, new SpongeInventory(e.container()));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	private InventoryAction getAction(ClickContainerEvent e) {
		if(e instanceof ClickContainerEvent.Double)
			return InventoryAction.DOUBLE;
		else if(e instanceof ClickContainerEvent.Creative)
			return InventoryAction.CREATIVE;
		else if(e instanceof ClickContainerEvent.Drop)
			return InventoryAction.DROP;
		else if(e instanceof ClickContainerEvent.Middle)
			return InventoryAction.MIDDLE;
		else if(e instanceof ClickContainerEvent.Shift.Primary)
			return InventoryAction.LEFT_SHIFT;
		else if(e instanceof ClickContainerEvent.Shift.Secondary)
			return InventoryAction.RIGHT_SHIFT;
		else if(e instanceof ClickContainerEvent.NumberPress)
			return InventoryAction.NUMBER;
		// Order matters! Primary and Secondary are at the end because other events may subclass them (e.g. Shift.Primary)
		else if(e instanceof ClickContainerEvent.Primary)
			return InventoryAction.LEFT;
		else if(e instanceof ClickContainerEvent.Secondary)
			return InventoryAction.RIGHT;
		return InventoryAction.UNKNOWN;
	}
	
	@Listener
	public void onInventoryClose(ClickContainerEvent.Close e, @First ServerPlayer p) {
		EventManager.callEvent(new InventoryCloseEvent(SpongeEntityManager.getPlayer(p), new SpongeInventory(e.container())));
	}
}
