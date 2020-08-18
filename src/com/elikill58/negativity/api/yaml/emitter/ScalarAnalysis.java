package com.elikill58.negativity.api.yaml.emitter;

public final class ScalarAnalysis {
	public String scalar;
	public boolean empty;
	public boolean multiline;
	public boolean allowFlowPlain;
	public boolean allowBlockPlain;
	public boolean allowSingleQuoted;
	public boolean allowBlock;

	public ScalarAnalysis(final String scalar, final boolean empty, final boolean multiline,
			final boolean allowFlowPlain, final boolean allowBlockPlain, final boolean allowSingleQuoted,
			final boolean allowBlock) {
		this.scalar = scalar;
		this.empty = empty;
		this.multiline = multiline;
		this.allowFlowPlain = allowFlowPlain;
		this.allowBlockPlain = allowBlockPlain;
		this.allowSingleQuoted = allowSingleQuoted;
		this.allowBlock = allowBlock;
	}
}
