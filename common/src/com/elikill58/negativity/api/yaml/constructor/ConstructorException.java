package com.elikill58.negativity.api.yaml.constructor;

import com.elikill58.negativity.api.yaml.error.Mark;
import com.elikill58.negativity.api.yaml.error.MarkedYAMLException;

public class ConstructorException extends MarkedYAMLException {
	private static final long serialVersionUID = -8816339931365239910L;

	protected ConstructorException(final String context, final Mark contextMark, final String problem,
			final Mark problemMark, final Throwable cause) {
		super(context, contextMark, problem, problemMark, cause);
	}

	protected ConstructorException(final String context, final Mark contextMark, final String problem,
			final Mark problemMark) {
		this(context, contextMark, problem, problemMark, (Throwable) null);
	}
}
