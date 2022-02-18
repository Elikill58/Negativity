package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class RedisNegativityMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 7;
	
	private String proxyId;
	private UUID uuid;
	private NegativityMessage message;
	
	public RedisNegativityMessage() {
		
	}
	
	public RedisNegativityMessage(UUID uuid, String proxyId, NegativityMessage message) {
		this.uuid = uuid;
		this.proxyId = proxyId;
		this.message = message;
	}
	
	public NegativityMessage getMessage() {
		return message;
	}

	public UUID getUUID() {
		return uuid;
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
		uuid = UUID.fromString(input.readUTF());
		proxyId = input.readUTF();
		message = NegativityMessagesManager.MESSAGES_BY_ID.get(input.readByte()).get();
		message.readFrom(input);
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeUTF(uuid.toString());
		output.writeUTF(proxyId);
		output.writeByte(message.messageId());
		message.writeTo(output);
	}
	
}
