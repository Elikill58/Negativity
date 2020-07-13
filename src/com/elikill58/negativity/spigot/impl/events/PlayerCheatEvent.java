package com.elikill58.negativity.spigot.impl.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.negativity.IPlayerCheatEvent;
import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatEvent extends Event implements IPlayerCheatEvent {

	private final Player p;
	private final Cheat c;
	private final int relia;
	
	public PlayerCheatEvent(Player p, Cheat c, int relia) {
		this.p = p;
		this.c = c;
		this.relia = relia;
	}

	@Override
	public Player getPlayer() {
		return p;
	}

	@Override
	public Cheat getCheat() {
		return c;
	}

	@Override
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
