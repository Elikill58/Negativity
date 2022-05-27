package com.elikill58.negativity.sponge7.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge7.impl.inventory.SpongeInventory;
import com.elikill58.negativity.sponge7.impl.item.SpongeItemStack;

public class InventoryListeners {
	
	@Listener
	public void onInventoryOpen(InteractInventoryEvent.Open e, @First Player p) {
		InventoryOpenEvent event = new InventoryOpenEvent(SpongeEntityManager.getPlayer(p));
		EventManager.callEvent(event);
		e.setCancelled(event.isCancelled());
	}
	
	@Listener
	public void onInvClick(ClickInventoryEvent e, @First Player p) {
		if (e.getTransactions().isEmpty())
			return;
		InventoryAction action = getAction(e);
		SlotTransaction transaction = e.getTransactions().get(0);
		ItemStack item = new SpongeItemStack(transaction.getOriginal().createStack());
		int slotIndex = transaction.getSlot().getInventoryProperty(SlotIndex.class).map(SlotIndex::getValue).orElse(-1);
		InventoryClickEvent event = new InventoryClickEvent(SpongeEntityManager.getPlayer(p), action, slotIndex, item, new SpongeInventory(e.getTargetInventory()));
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
		else if(e instanceof ClickInventoryEvent.Shift.Primary)
			return InventoryAction.LEFT_SHIFT;
		else if(e instanceof ClickInventoryEvent.Shift.Secondary)
			return InventoryAction.RIGHT_SHIFT;
		else if(e instanceof ClickInventoryEvent.NumberPress)
			return InventoryAction.NUMBER;
		// Order matters! Primary and Secondary are at the end because other events may subclass them (e.g. Shift.Primary)
		else if(e instanceof ClickInventoryEvent.Primary)
			return InventoryAction.LEFT;
		else if(e instanceof ClickInventoryEvent.Secondary)
			return InventoryAction.RIGHT;
		return InventoryAction.UNKNOWN;
	}
	
	@Listener
	public void onInventoryClose(InteractInventoryEvent.Close e, @First Player p) {
		EventManager.callEvent(new InventoryCloseEvent(SpongeEntityManager.getPlayer(p), new SpongeInventory(e.getTargetInventory())));
	}
}
