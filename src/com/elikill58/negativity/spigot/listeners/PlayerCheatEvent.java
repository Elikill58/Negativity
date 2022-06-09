package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatEvent extends PlayerEvent {

	private Cheat c;
	private int relia;
	
	public PlayerCheatEvent(Player p, Cheat c, int reliability) {
		super(p);
		this.c = c;
		this.relia = reliability;
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
