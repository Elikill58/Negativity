package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class PlayerPacketsClearEvent extends Event {
	
	private Player p;
	private SpigotNegativityPlayer np;
	
	public PlayerPacketsClearEvent(Player p, SpigotNegativityPlayer np) {
		this.p = p;
		this.np = np;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public SpigotNegativityPlayer getNegativityPlayer() {
		return np;
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
