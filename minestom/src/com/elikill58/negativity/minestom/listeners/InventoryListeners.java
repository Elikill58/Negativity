package com.elikill58.negativity.minestom.listeners;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryOpenEvent;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;
import com.elikill58.negativity.minestom.impl.inventory.MinestomInventory;
import com.elikill58.negativity.minestom.impl.item.MinestomItemStack;

import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.inventory.click.Click;

public class InventoryListeners {

	public InventoryListeners(EventNode<Event> e) {
		e.addListener(net.minestom.server.event.inventory.InventoryOpenEvent.class, this::onInventoryOpen);
		e.addListener(net.minestom.server.event.inventory.InventoryClickEvent.class, this::onInventoryClick);
	}
	
	public void onInventoryOpen(net.minestom.server.event.inventory.InventoryOpenEvent e) {
		if(e.isCancelled())
			return;
		InventoryOpenEvent event = new InventoryOpenEvent(MinestomEntityManager.getPlayer(e.getPlayer()));
		EventManager.callEvent(event);
		if(event.isCancelled())
			e.setCancelled(event.isCancelled());
	}
	
	public void onInventoryClick(net.minestom.server.event.inventory.InventoryClickEvent e) {
		if(e.getInventory() == null || e.getChanges().isEmpty())
			return;
		e.getChanges().forEach(c -> {
			if(c instanceof Click.Change.Container cc)
				EventManager.callEvent(new InventoryClickEvent(MinestomEntityManager.getPlayer(e.getPlayer()), getAction(e.getClickInfo()), cc.slot(), new MinestomItemStack(cc.item()), new MinestomInventory(e.getInventory())));
			if(c instanceof Click.Change.Player cc)
				EventManager.callEvent(new InventoryClickEvent(MinestomEntityManager.getPlayer(e.getPlayer()), getAction(e.getClickInfo()), cc.slot(), new MinestomItemStack(cc.item()), new MinestomInventory(e.getInventory())));
		});
	}
	
	private InventoryAction getAction(Click.Info type) {
		if(type instanceof Click.Info.DropSlot)
			return InventoryAction.DROP;
		if(type instanceof Click.Info.Double)
			return InventoryAction.DOUBLE;
		if(type instanceof Click.Info.Right)
			return InventoryAction.RIGHT;
		if(type instanceof Click.Info.Left)
			return InventoryAction.LEFT;
		if(type instanceof Click.Info.RightShift)
			return InventoryAction.RIGHT_SHIFT;
		if(type instanceof Click.Info.LeftShift)
			return InventoryAction.LEFT_SHIFT;
		if(type instanceof Click.Info.CreativeDropItem || type instanceof Click.Info.CreativeSetItem)
			return InventoryAction.CREATIVE;
		if(type instanceof Click.Info.Middle || type instanceof Click.Info.MiddleDrag || type instanceof Click.Info.MiddleDropCursor)
			return InventoryAction.MIDDLE;
		return InventoryAction.UNKNOWN;
	}
	
	public void onInventoryClose(InventoryCloseEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.inventory.InventoryCloseEvent(MinestomEntityManager.getPlayer(e.getPlayer()), new MinestomInventory(e.getInventory())));
	}
}
