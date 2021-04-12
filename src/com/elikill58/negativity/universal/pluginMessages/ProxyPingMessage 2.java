package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProxyPingMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 1;

	private int protocol = 0;

	public ProxyPingMessage() {
	}

	public ProxyPingMessage(int protocol) {
		this.protocol = protocol;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		protocol = input.readInt();
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeInt(protocol);
	}

	public int getProtocol() {
		return protocol;
	}
}
