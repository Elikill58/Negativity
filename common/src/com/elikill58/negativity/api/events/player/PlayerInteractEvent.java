package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerInteractEvent extends PlayerEvent implements CancellableEvent {
	
	private final Action action;
	private boolean cancel = false;
	
	public PlayerInteractEvent(Player p, Action action) {
		super(p);
		this.action = action;
	}
	
	public Action getAction() {
		return action;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
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
