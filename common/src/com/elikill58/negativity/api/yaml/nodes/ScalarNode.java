package com.elikill58.negativity.api.yaml.nodes;

import com.elikill58.negativity.api.yaml.error.Mark;

public class ScalarNode extends Node {
	private Character style;
	private String value;

	public ScalarNode(final Tag tag, final String value, final Mark startMark, final Mark endMark,
			final Character style) {
		this(tag, true, value, startMark, endMark, style);
	}

	public ScalarNode(final Tag tag, final boolean resolved, final String value, final Mark startMark,
			final Mark endMark, final Character style) {
		super(tag, startMark, endMark);
		if (value == null) {
			throw new NullPointerException("value in a Node is required.");
		}
		this.value = value;
		this.style = style;
		this.resolved = resolved;
	}

	public Character getStyle() {
		return this.style;
	}

	@Override
	public NodeId getNodeId() {
		return NodeId.scalar;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
	}
}
