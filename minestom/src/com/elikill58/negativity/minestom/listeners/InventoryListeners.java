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
import net.minestom.server.inventory.click.ClickType;

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
		if(e.getInventory() == null || e.getClickedItem() == null)
			return;
		EventManager.callEvent(new InventoryClickEvent(MinestomEntityManager.getPlayer(e.getPlayer()), getAction(e.getClickType()), e.getSlot(), new MinestomItemStack(e.getClickedItem()), new MinestomInventory(e.getInventory())));
	}
	
	private InventoryAction getAction(ClickType type) {
		switch(type) {
		case CHANGE_HELD:
			break;
		case DOUBLE_CLICK:
			return InventoryAction.DOUBLE;
		case DROP:
			return InventoryAction.DROP;
		case END_LEFT_DRAGGING:
			break;
		case END_RIGHT_DRAGGING:
			break;
		case LEFT_CLICK:
			return InventoryAction.LEFT;
		case LEFT_DRAGGING:
			break;
		case RIGHT_CLICK:
			return InventoryAction.RIGHT;
		case RIGHT_DRAGGING:
			break;
		case SHIFT_CLICK:
			return InventoryAction.LEFT_SHIFT;
		case START_DOUBLE_CLICK:
			break;
		case START_LEFT_DRAGGING:
			break;
		case START_RIGHT_DRAGGING:
			break;
		case START_SHIFT_CLICK:
			break;
		default:
			break;
		}
		return InventoryAction.UNKNOWN;
	}
	
	public void onInventoryClose(InventoryCloseEvent e) {
		EventManager.callEvent(new com.elikill58.negativity.api.events.inventory.InventoryCloseEvent(MinestomEntityManager.getPlayer(e.getPlayer()), new MinestomInventory(e.getInventory())));
	}
}
