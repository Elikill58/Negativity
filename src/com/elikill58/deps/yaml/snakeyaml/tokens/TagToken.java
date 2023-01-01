package com.elikill58.deps.yaml.snakeyaml.tokens;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public final class TagToken extends Token {
	private final TagTuple value;

	public TagToken(final TagTuple value, final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
		this.value = value;
	}

	public TagTuple getValue() {
		return this.value;
	}

	@Override
	protected String getArguments() {
		return "value=[" + this.value.getHandle() + ", " + this.value.getSuffix() + "]";
	}

	@Override
	public ID getTokenId() {
		return ID.Tag;
	}
}
