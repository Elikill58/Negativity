package com.elikill58.negativity.api.yaml.tokens;

import com.elikill58.negativity.api.yaml.error.Mark;

public class WhitespaceToken extends Token {
	public WhitespaceToken(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public ID getTokenId() {
		return ID.Whitespace;
	}
}
