package com.elikill58.negativity.api.events;

import java.util.function.BiConsumer;

public interface BakedListeners {
	
	void bakeListeners(BiConsumer<Class<? extends Event>, ListenerCaller> registrator);
}
