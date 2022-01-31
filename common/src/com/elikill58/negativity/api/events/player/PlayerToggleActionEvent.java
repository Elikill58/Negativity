package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerToggleActionEvent extends PlayerEvent implements CancellableEvent {

	private final ToggleAction action;
	private boolean cancel;
	
	public PlayerToggleActionEvent(Player p, ToggleAction action, boolean cancel) {
		super(p);
		this.action = action;
		this.cancel = cancel;
	}
	
	public ToggleAction getAction() {
		return action;
	}
	
	public static enum ToggleAction {
		FLY,
		SNEAK,
		SPRINT;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean b) {
		this.cancel = b;
	}
}
