package com.elikill58.negativity.api.yaml.nodes;

import java.util.List;

import com.elikill58.negativity.api.yaml.error.Mark;

public class SequenceNode extends CollectionNode<Node> {
	private final List<Node> value;

	public SequenceNode(final Tag tag, final boolean resolved, final List<Node> value, final Mark startMark,
			final Mark endMark, final Boolean flowStyle) {
		super(tag, startMark, endMark, flowStyle);
		if (value == null) {
			throw new NullPointerException("value in a Node is required.");
		}
		this.value = value;
		this.resolved = resolved;
	}

	public SequenceNode(final Tag tag, final List<Node> value, final Boolean flowStyle) {
		this(tag, true, value, null, null, flowStyle);
	}

	@Override
	public NodeId getNodeId() {
		return NodeId.sequence;
	}

	@Override
	public List<Node> getValue() {
		return this.value;
	}

	public void setListType(final Class<?> listType) {
		for (final Node node : this.value) {
			node.setType(listType);
		}
	}

	@Override
	public String toString() {
		return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
	}
}
