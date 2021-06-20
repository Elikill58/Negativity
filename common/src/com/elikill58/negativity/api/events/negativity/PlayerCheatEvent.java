package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatEvent extends PlayerEvent {

	private final Cheat c;
	private final int relia;
	
	public PlayerCheatEvent(Player p, Cheat c, int relia) {
		super(p);
		this.c = c;
		this.relia = relia;
	}

	public Cheat getCheat() {
		return c;
	}

	public int getReliability() {
		return relia;
	}
}
