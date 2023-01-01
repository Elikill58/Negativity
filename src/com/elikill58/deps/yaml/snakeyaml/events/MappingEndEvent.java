package com.elikill58.deps.yaml.snakeyaml.events;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public final class MappingEndEvent extends CollectionEndEvent {
	public MappingEndEvent(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public boolean is(final ID id) {
		return ID.MappingEnd == id;
	}
}
