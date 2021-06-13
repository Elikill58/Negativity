package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerLeaveEvent extends PlayerEvent {

	private final NegativityPlayer np;
	private String quitMessage;
	
	public PlayerLeaveEvent(Player p, NegativityPlayer np, String quitMessage) {
		super(p);
		this.np = np;
		this.quitMessage = quitMessage;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public String getQuitMessage() {
		return quitMessage;
	}
	
	public void setQuitMessage(String quitMessage) {
		this.quitMessage = quitMessage;
	}
}
