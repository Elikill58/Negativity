package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerChatEvent extends PlayerEvent implements CancellableEvent {
	
	private String message;
	private boolean cancel;
	
	public PlayerChatEvent(Player p, String message) {
		super(p);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
