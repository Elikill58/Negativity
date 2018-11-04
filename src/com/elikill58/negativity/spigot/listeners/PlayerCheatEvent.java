package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.utils.Cheat;

public class PlayerCheatEvent extends Event {

	private Player p;
	private Cheat c;
	private int relia;
	
	public PlayerCheatEvent(Player p, Cheat c, int reliability) {
		this.p = p;
		this.c = c;
		this.relia = reliability;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Cheat getCheat() {
		return c;
	}
	
	public int getReliability() {
		return relia;
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
