package com.elikill58.negativity.api.yaml.constructor;

import com.elikill58.negativity.api.yaml.nodes.Node;

public interface Construct {
	Object construct(final Node p0);

	void construct2ndStep(final Node p0, final Object p1);
}
