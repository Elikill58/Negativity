package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Base of all negativity plugin messages
 */
public interface NegativityMessage {

	/**
	 * Get the negativity message ID
	 * 
	 * @return the negativity message ID
	 */
	byte messageId();

	/**
	 * Read the message from the input stream
	 * 
	 * @param input the input stream which contains the negativity message
	 * @throws IOException if there is an exception
	 */
	void readFrom(DataInputStream input) throws IOException;

	/**
	 * Write the message from the input stream
	 * 
	 * @param output the output stream which contains the negativity message
	 * @throws IOException if there is an exception
	 */
	void writeTo(DataOutputStream output) throws IOException;
}
