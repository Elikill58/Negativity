package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerCommandPreProcessEvent extends PlayerEvent implements CancellableEvent {

	private final String[] arg;
	private final String command, prefix;
	private boolean cancelled = false, proxy = false;

	public PlayerCommandPreProcessEvent(Player p, String command, String[] arg, String prefix, boolean proxy) {
		super(p);
		this.command = command;
		this.arg = arg;
		this.prefix = prefix;
		this.proxy = proxy;
	}

	public String getCommand() {
		return command;
	}

	public String[] getArgument() {
		return arg;
	}

	public String getPrefix() {
		return prefix;
	}
	
	/**
	 * Check if it's run on proxy or not
	 * 
	 * @return true if from proxy
	 */
	public boolean isProxy() {
		return proxy;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}
	
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
