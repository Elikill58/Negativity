package com.elikill58.negativity.api.events.negativity;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;

public interface IShowAlertPermissionEvent extends Event {
	
	public Player getPlayer();
	
	public NegativityPlayer getNegativityPlayer();
	
	public boolean hasBasicPerm();
	
	public boolean hasPerm();
	
	public boolean isCancelled();

	public void setCancelled(boolean c);

}
