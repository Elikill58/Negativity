package com.elikill58.negativity.spigot.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerMoveEvent;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class NegativityPlayerMoveEvent extends Event {

	private final SpigotNegativityPlayer np;
	private final PlayerMoveEvent event;
	
	public NegativityPlayerMoveEvent(PlayerMoveEvent e) {
		this.event = e;
		this.np = SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer());
	}
	
	public SpigotNegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public Player getPlayer() {
		return event.getPlayer();
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
	
	public boolean isCancelled() {
		return event.isCancelled();
	}
	
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
