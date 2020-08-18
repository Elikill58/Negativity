package com.elikill58.negativity.api.yaml.tokens;

import com.elikill58.negativity.api.yaml.error.Mark;

public final class BlockEntryToken extends Token {
	public BlockEntryToken(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public ID getTokenId() {
		return ID.BlockEntry;
	}
}
