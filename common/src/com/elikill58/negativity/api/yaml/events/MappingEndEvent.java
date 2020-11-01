package com.elikill58.negativity.api.yaml.events;

import com.elikill58.negativity.api.yaml.error.Mark;

public final class MappingEndEvent extends CollectionEndEvent {
	public MappingEndEvent(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public boolean is(final ID id) {
		return ID.MappingEnd == id;
	}
}
