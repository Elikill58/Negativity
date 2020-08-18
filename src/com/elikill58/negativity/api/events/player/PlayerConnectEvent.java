package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;

public class PlayerConnectEvent implements Event {

	private final Player p;
	private final NegativityPlayer np;
	private String joinMessage;
	
	public PlayerConnectEvent(Player p, NegativityPlayer np, String joinMessage) {
		this.p = p;
		this.np = np;
		this.joinMessage = joinMessage;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public String getJoinMessage() {
		return joinMessage;
	}
	
	public void setJoinMessage(String joinMessage) {
		this.joinMessage = joinMessage;
	}
}
