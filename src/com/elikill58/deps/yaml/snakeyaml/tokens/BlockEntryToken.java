package com.elikill58.deps.yaml.snakeyaml.tokens;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public final class BlockEntryToken extends Token {
	public BlockEntryToken(final Mark startMark, final Mark endMark) {
		super(startMark, endMark);
	}

	@Override
	public ID getTokenId() {
		return ID.BlockEntry;
	}
}
