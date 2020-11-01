package com.elikill58.negativity.api.yaml.resolver;

import java.util.regex.Pattern;

import com.elikill58.negativity.api.yaml.nodes.Tag;

final class ResolverTuple {
	private final Tag tag;
	private final Pattern regexp;

	public ResolverTuple(final Tag tag, final Pattern regexp) {
		this.tag = tag;
		this.regexp = regexp;
	}

	public Tag getTag() {
		return this.tag;
	}

	public Pattern getRegexp() {
		return this.regexp;
	}

	@Override
	public String toString() {
		return "Tuple tag=" + this.tag + " regexp=" + this.regexp;
	}
}
