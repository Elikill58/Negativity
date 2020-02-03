package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class ProxyPingMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 1;

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) {
	}

	@Override
	public void writeTo(DataOutputStream output) {
	}
}
