package com.elikill58.deps.yaml.snakeyaml.tokens;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public final class AliasToken extends Token {
	private final String value;

	public AliasToken(final String value, final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	protected String getArguments() {
		return "value=" + this.value;
	}

	@Override
	public ID getTokenId() {
		return ID.Alias;
	}
}
