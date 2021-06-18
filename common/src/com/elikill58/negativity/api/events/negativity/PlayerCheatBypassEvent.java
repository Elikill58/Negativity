package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatBypassEvent extends PlayerEvent {

	private final Player p;
	private final Cheat c;
	private final int relia;
	private boolean cancel = false;
	
	public PlayerCheatBypassEvent(Player p, Cheat c, int reliability) {
		super(p);
		this.p = p;
		this.c = c;
		this.relia = reliability;
	}
	
	public Cheat getCheat() {
		return c;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public int getReliability() {
		return relia;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
