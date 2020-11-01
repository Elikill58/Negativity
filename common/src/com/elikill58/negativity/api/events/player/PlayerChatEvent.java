package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;

public class PlayerChatEvent implements Event {
	
	private final Player p;
	private String message, format;
	private boolean cancel;
	
	public PlayerChatEvent(Player p, String message, String format) {
		this.p = p;
		this.message = message;
		this.format = format;
	}

	public Player getPlayer() {
		return p;
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
