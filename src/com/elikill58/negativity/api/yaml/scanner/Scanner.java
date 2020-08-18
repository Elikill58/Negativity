package com.elikill58.negativity.api.yaml.scanner;

import com.elikill58.negativity.api.yaml.tokens.Token;

public interface Scanner {
	boolean checkToken(final Token.ID... p0);

	Token peekToken();

	Token getToken();
}
