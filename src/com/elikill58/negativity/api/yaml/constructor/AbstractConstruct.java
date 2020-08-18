package com.elikill58.negativity.api.yaml.constructor;

import com.elikill58.negativity.api.yaml.error.YAMLException;
import com.elikill58.negativity.api.yaml.nodes.Node;

public abstract class AbstractConstruct implements Construct {
	@Override
	public void construct2ndStep(final Node node, final Object data) {
		if (node.isTwoStepsConstruction()) {
			throw new IllegalStateException("Not Implemented in " + this.getClass().getName());
		}
		throw new YAMLException("Unexpected recursive structure for Node: " + node);
	}
}
