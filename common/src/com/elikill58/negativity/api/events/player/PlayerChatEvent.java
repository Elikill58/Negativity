package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerChatEvent extends PlayerEvent {
	
	private String message, format;
	private boolean cancel;
	
	public PlayerChatEvent(Player p, String message, String format) {
		super(p);
		this.message = message;
		this.format = format;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public boolean isCancelled() {
		return cancel;
	}

	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
}
