package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerConnectEvent extends PlayerEvent {

	private final NegativityPlayer np;
	private String joinMessage;
	
	public PlayerConnectEvent(Player p, NegativityPlayer np, String joinMessage) {
		super(p);
		this.np = np;
		this.joinMessage = joinMessage;
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
