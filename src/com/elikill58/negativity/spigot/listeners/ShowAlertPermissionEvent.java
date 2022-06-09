package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class ShowAlertPermissionEvent extends PlayerEvent implements Cancellable {

	private final boolean basicPerm;
	private final SpigotNegativityPlayer np;
	private boolean cancel = false;
	
	public ShowAlertPermissionEvent(Player p, SpigotNegativityPlayer np, boolean hasBasicPerm) {
		super(p);
		this.np = np;
		this.basicPerm = hasBasicPerm;
	}
	
	public SpigotNegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public boolean hasBasicPerm() {
		return basicPerm;
	}
	
	public boolean hasPerm() {
		return !isCancelled() && basicPerm;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean c) {
		cancel = c;
	}
}
