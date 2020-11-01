package com.elikill58.negativity.api.yaml.nodes;

public class AnchorNode extends Node {
	private Node realNode;

	public AnchorNode(final Node realNode) {
		super(realNode.getTag(), realNode.getStartMark(), realNode.getEndMark());
		this.realNode = realNode;
	}

	@Override
	public NodeId getNodeId() {
		return NodeId.anchor;
	}

	public Node getRealNode() {
		return this.realNode;
	}
}
