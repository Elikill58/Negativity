package com.elikill58.negativity.api.yaml.parser;

import com.elikill58.negativity.api.yaml.error.Mark;
import com.elikill58.negativity.api.yaml.error.MarkedYAMLException;

public class ParserException extends MarkedYAMLException {
	private static final long serialVersionUID = -2349253802798398038L;

	public ParserException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
		super(context, contextMark, problem, problemMark, null, null);
	}
}
