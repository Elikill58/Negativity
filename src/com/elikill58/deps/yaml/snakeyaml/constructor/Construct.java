package com.elikill58.deps.yaml.snakeyaml.constructor;

import com.elikill58.deps.yaml.snakeyaml.nodes.Node;

public interface Construct {
	Object construct(final Node p0);

	void construct2ndStep(final Node p0, final Object p1);
}
