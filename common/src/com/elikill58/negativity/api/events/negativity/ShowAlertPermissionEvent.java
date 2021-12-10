package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.events.PlayerEvent;

public class ShowAlertPermissionEvent extends PlayerEvent implements CancellableEvent {

	private final boolean basicPerm;
	private final NegativityPlayer np;
	private boolean cancel = false;
	
	public ShowAlertPermissionEvent(Player p, NegativityPlayer np, boolean hasBasicPerm) {
		super(p);
		this.np = np;
		this.basicPerm = hasBasicPerm;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	/**
	 * Know if the player has the basic permission to see alert
	 * 
	 * @return true if player have showAlert permission
	 */
	public boolean hasBasicPerm() {
		return basicPerm;
	}
	
	/**
	 * Check if a player has the permission to see alert.
	 * Count if it's cancelled and if the player has the needed permission
	 * 
	 * @return true if can see alert
	 */
	public boolean hasPerm() {
		return !isCancelled() && basicPerm;
	}
	
	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean c) {
		cancel = c;
	}
}
