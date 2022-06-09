package com.elikill58.negativity.spigot.listeners;

import org.bukkit.Location;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class NegativityPlayerMoveEvent extends PlayerEvent implements Cancellable {

	private final SpigotNegativityPlayer np;
	private final PlayerMoveEvent event;
	
	public NegativityPlayerMoveEvent(PlayerMoveEvent e) {
		super(e.getPlayer());
		this.event = e;
		this.np = SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer());
	}
	
	public SpigotNegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public Location getTo() {
		return event.getTo();
	}
	
	public void setTo(Location to) {
		event.setTo(to);
	}
	
	public Location getFrom() {
		return event.getFrom();
	}
	
	public void setFrom(Location from) {
		event.setFrom(from);
	}
	
	@Override
	public boolean isCancelled() {
		return event.isCancelled();
	}
	
	@Override
	public void setCancelled(boolean b) {
		event.setCancelled(b);
	}
	
	public PlayerMoveEvent getEvent() {
		return event;
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
