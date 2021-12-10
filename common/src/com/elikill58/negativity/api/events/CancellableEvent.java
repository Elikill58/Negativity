package com.elikill58.negativity.api.events;

public interface CancellableEvent extends Event {

	public boolean isCancelled();
	
	public void setCancelled(boolean b);
}
