package com.elikill58.deps.yaml.snakeyaml.events;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public abstract class CollectionEndEvent extends Event {
	public CollectionEndEvent(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}
}
