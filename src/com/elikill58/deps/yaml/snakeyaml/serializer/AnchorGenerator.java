package com.elikill58.deps.yaml.snakeyaml.serializer;

import com.elikill58.deps.yaml.snakeyaml.nodes.Node;

public interface AnchorGenerator
{
    String nextAnchor(final Node p0);
}
