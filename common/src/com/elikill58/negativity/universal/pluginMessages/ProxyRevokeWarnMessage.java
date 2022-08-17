package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ProxyRevokeWarnMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 11;

	private UUID playerId;

	public ProxyRevokeWarnMessage() {
	}

	public ProxyRevokeWarnMessage(UUID playerId) {
		this.playerId = playerId;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		this.playerId = new UUID(input.readLong(), input.readLong());
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeLong(playerId.getMostSignificantBits());
		output.writeLong(playerId.getLeastSignificantBits());
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public UUID getPlayerId() {
		return playerId;
	}
}
