package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RedisNegativityMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 7;
	
	private String server, proxyId;
	private NegativityMessage message;
	
	public RedisNegativityMessage() {
		
	}
	
	public RedisNegativityMessage(String server, String proxyId, NegativityMessage message) {
		this.server = server;
		this.proxyId = proxyId;
		this.message = message;
	}
	
	public NegativityMessage getMessage() {
		return message;
	}
	
	public String getServer() {
		return server;
	}
	
	public String getProxyId() {
		return proxyId;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		server = input.readUTF();
		proxyId = input.readUTF();
		message = NegativityMessagesManager.readMessage(input);
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeUTF(server);
		output.writeUTF(proxyId);
		output.write(NegativityMessagesManager.writeMessage(message));
	}
	
}
