package com.elikill58.deps.yaml.snakeyaml.serializer;

import java.text.NumberFormat;

import com.elikill58.deps.yaml.snakeyaml.nodes.Node;

public class NumberAnchorGenerator implements AnchorGenerator {
	private int lastAnchorId;

	public NumberAnchorGenerator(final int lastAnchorId) {
		this.lastAnchorId = 0;
		this.lastAnchorId = lastAnchorId;
	}

	@Override
	public String nextAnchor(final Node node) {
		++this.lastAnchorId;
		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumIntegerDigits(3);
		format.setMaximumFractionDigits(0);
		format.setGroupingUsed(false);
		final String anchorId = format.format(this.lastAnchorId);
		return "id" + anchorId;
	}
}
