package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;

public class PlayerInteractEvent implements Event {
	
	private final Player p;
	private final Action action;
	private boolean cancel = false;
	
	public PlayerInteractEvent(Player p, Action action) {
		this.p = p;
		this.action = action;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public Action getAction() {
		return action;
	}
	
	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean b) {
		this.cancel = b;
	}
	
	public enum Action {
		RIGHT_CLICK_AIR,
		RIGHT_CLICK_BLOCK,
		LEFT_CLICK_AIR,
		LEFT_CLICK_BLOCK,
		PHYSICAL
	}
}
