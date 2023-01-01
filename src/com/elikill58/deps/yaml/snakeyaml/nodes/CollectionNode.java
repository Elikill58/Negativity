package com.elikill58.deps.yaml.snakeyaml.nodes;

import java.util.List;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;

public abstract class CollectionNode<T> extends Node {
	private Boolean flowStyle;

	public CollectionNode(final Tag tag, final Mark startMark, final Mark endMark, final Boolean flowStyle) {
		super(tag, startMark, endMark);
		this.flowStyle = flowStyle;
	}

	public abstract List<T> getValue();

	public Boolean getFlowStyle() {
		return this.flowStyle;
	}

	public void setFlowStyle(final Boolean flowStyle) {
		this.flowStyle = flowStyle;
	}

	public void setEndMark(final Mark endMark) {
		this.endMark = endMark;
	}
}
