package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatKickEvent implements Event {

	private boolean cancel = false;
	private Player p;
	private Cheat c;
	private int relia;
	
	public PlayerCheatKickEvent(Player p, Cheat c, int reliability) {
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
	
	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
