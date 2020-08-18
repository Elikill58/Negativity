package com.elikill58.negativity.api.yaml.events;

import com.elikill58.negativity.api.yaml.error.Mark;

public final class StreamEndEvent extends Event {
	public StreamEndEvent(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public boolean is(final ID id) {
		return ID.StreamEnd == id;
	}
}
