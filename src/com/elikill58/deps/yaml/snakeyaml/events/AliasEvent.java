package com.elikill58.deps.yaml.snakeyaml.events;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public final class AliasEvent extends NodeEvent {
	public AliasEvent(final String anchor, final Mark startMark, final Mark endMark) {
		super(anchor, startMark, endMark);
	}

	@Override
	public boolean is(final ID id) {
		return ID.Alias == id;
	}
}
