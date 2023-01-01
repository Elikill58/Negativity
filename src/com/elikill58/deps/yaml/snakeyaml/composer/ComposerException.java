package com.elikill58.deps.yaml.snakeyaml.composer;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;
import com.elikill58.deps.yaml.snakeyaml.error.MarkedYAMLException;

public class ComposerException extends MarkedYAMLException {
	private static final long serialVersionUID = 2146314636913113935L;

	protected ComposerException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
		super(context, contextMark, problem, problemMark);
	}
}
