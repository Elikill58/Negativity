package com.elikill58.negativity.api.events;

import java.lang.reflect.Method;

public interface ListenerCaller {
	
	void call(Event event);

	default Method getMethod() {
		return null;
	}
}
