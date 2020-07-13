package com.elikill58.negativity.spigot.impl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.negativity.IShowAlertPermissionEvent;

public class ShowAlertPermissionEvent extends Event implements IShowAlertPermissionEvent {

	private final boolean basicPerm;
	private final Player p;
	private final NegativityPlayer np;
	private boolean cancel = false;
	
	public ShowAlertPermissionEvent(Player p, NegativityPlayer np, boolean hasBasicPerm) {
		this.p = p;
		this.np = np;
		this.basicPerm = hasBasicPerm;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public boolean hasBasicPerm() {
		return basicPerm;
	}
	
	public boolean hasPerm() {
		return !isCancelled() && basicPerm;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean c) {
		cancel = c;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
