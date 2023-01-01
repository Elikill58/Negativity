package com.elikill58.deps.yaml.snakeyaml.tokens;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;
import com.elikill58.deps.yaml.snakeyaml.error.YAMLException;

public abstract class Token {
	private final Mark startMark;
	private final Mark endMark;

	public Token(final Mark startMark, final Mark endMark) {
		if (startMark == null || endMark == null) {
			throw new YAMLException("Token requires marks.");
		}
		this.startMark = startMark;
		this.endMark = endMark;
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getName() + "(" + this.getArguments() + ")>";
	}

	public Mark getStartMark() {
		return this.startMark;
	}

	public Mark getEndMark() {
		return this.endMark;
	}

	protected String getArguments() {
		return "";
	}

	public abstract ID getTokenId();

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Token && this.toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public enum ID {
		Alias, Anchor, BlockEnd, BlockEntry, BlockMappingStart, BlockSequenceStart, Directive, DocumentEnd, DocumentStart, FlowEntry, FlowMappingEnd, FlowMappingStart, FlowSequenceEnd,
		FlowSequenceStart, Key, Scalar, StreamEnd, StreamStart, Tag, Value, Whitespace, Comment, Error;
	}
}
