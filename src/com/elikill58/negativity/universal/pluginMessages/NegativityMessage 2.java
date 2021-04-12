package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base of all negativity plugin messages
 */
public interface NegativityMessage {

	byte messageId();

	void readFrom(DataInputStream input) throws IOException;

	void writeTo(DataOutputStream output) throws IOException;
}
