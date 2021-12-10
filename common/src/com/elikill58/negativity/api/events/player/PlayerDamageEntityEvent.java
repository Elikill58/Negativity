package com.elikill58.negativity.api.events.player;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class PlayerDamageEntityEvent extends PlayerEvent implements CancellableEvent {

	private boolean cancel;
	private final Entity damaged;
	private final boolean sync;
	
	public PlayerDamageEntityEvent(Player player, Entity damaged, boolean sync) {
		super(player);
		this.damaged = damaged;
		this.sync = sync;
	}

	public Entity getDamaged() {
		return damaged;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
	/**
	 * Of this is sync, the entity wasn't founded, and so it can have so tick of late.
	 * 
	 * @return true if call sync
	 */
	public boolean isSync() {
		return sync;
	}
}
