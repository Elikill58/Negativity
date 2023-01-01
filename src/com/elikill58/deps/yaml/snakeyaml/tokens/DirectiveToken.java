package com.elikill58.deps.yaml.snakeyaml.tokens;

import java.util.List;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;
import com.elikill58.deps.yaml.snakeyaml.error.YAMLException;

public final class DirectiveToken<T> extends Token {
	private final String name;
	private final List<T> value;

	public DirectiveToken(final String name, final List<T> value, final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
		this.name = name;
		if (value != null && value.size() != 2) {
			throw new YAMLException("Two strings must be provided instead of " + String.valueOf(value.size()));
		}
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public List<T> getValue() {
		return this.value;
	}

	@Override
	protected String getArguments() {
		if (this.value != null) {
			return "name=" + this.name + ", value=[" + this.value.get(0) + ", " + this.value.get(1) + "]";
		}
		return "name=" + this.name;
	}

	@Override
	public ID getTokenId() {
		return ID.Directive;
	}
}
