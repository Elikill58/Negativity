package com.elikill58.negativity.api.yaml.events;

import java.util.Map;

import com.elikill58.negativity.api.yaml.DumperOptions;
import com.elikill58.negativity.api.yaml.error.Mark;

public final class DocumentStartEvent extends Event {
	private final boolean explicit;
	private final DumperOptions.Version version;
	private final Map<String, String> tags;

	public DocumentStartEvent(final Mark startMark, final Mark endMark, final boolean explicit,
			final DumperOptions.Version version, final Map<String, String> tags) {
		super(startMark, endMark);
		this.explicit = explicit;
		this.version = version;
		this.tags = tags;
	}

	public boolean getExplicit() {
		return this.explicit;
	}

	public DumperOptions.Version getVersion() {
		return this.version;
	}

	public Map<String, String> getTags() {
		return this.tags;
	}

	@Override
	public boolean is(final ID id) {
		return ID.DocumentStart == id;
	}
}
