package com.elikill58.negativity.api.events;

public interface Listener<E extends Event> {
	
	void call(E event);
}
