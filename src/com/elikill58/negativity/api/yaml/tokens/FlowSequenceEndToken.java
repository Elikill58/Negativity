package com.elikill58.negativity.api.yaml.tokens;

import com.elikill58.negativity.api.yaml.error.Mark;

public final class FlowSequenceEndToken extends Token {
	public FlowSequenceEndToken(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public ID getTokenId() {
		return ID.FlowSequenceEnd;
	}
}
