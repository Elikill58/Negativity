package com.elikill58.negativity.api.yaml.events;

import com.elikill58.negativity.api.yaml.error.Mark;

public final class DocumentEndEvent extends Event {
	private final boolean explicit;

	public DocumentEndEvent(final Mark startMark, final Mark endMark, final boolean explicit) {
		super(startMark, endMark);
		this.explicit = explicit;
	}

	public boolean getExplicit() {
		return this.explicit;
	}

	@Override
	public boolean is(final ID id) {
		return ID.DocumentEnd == id;
	}
}
