package com.elikill58.negativity.api.yaml.composer;

import com.elikill58.negativity.api.yaml.error.Mark;
import com.elikill58.negativity.api.yaml.error.MarkedYAMLException;

public class ComposerException extends MarkedYAMLException {
	private static final long serialVersionUID = 2146314636913113935L;

	protected ComposerException(final String context, final Mark contextMark, final String problem,
			final Mark problemMark) {
		super(context, contextMark, problem, problemMark);
	}
}
