package com.elikill58.negativity.common.events.negativity;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;

public interface IShowAlertPermissionEvent extends Event {
	
	public Player getPlayer();
	
	public NegativityPlayer getNegativityPlayer();
	
	public boolean hasBasicPerm();
	
	public boolean hasPerm();
	
	public boolean isCancelled();

	public void setCancelled(boolean c);

}
