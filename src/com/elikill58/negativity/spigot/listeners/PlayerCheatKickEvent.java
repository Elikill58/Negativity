package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatKickEvent  extends PlayerEvent implements Cancellable {

	private boolean cancel = false;
	private Cheat c;
	private int relia;
	
	public PlayerCheatKickEvent(Player p, Cheat c, int reliability) {
		super(p);
		this.c = c;
		this.relia = reliability;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
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
