package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ProxyRevokeWarnMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 11;

	private UUID playerId;
	private String revoker;

	public ProxyRevokeWarnMessage() {
	}

	public ProxyRevokeWarnMessage(UUID playerId, String revoker) {
		this.playerId = playerId;
		this.revoker = revoker;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		this.playerId = new UUID(input.readLong(), input.readLong());
		this.revoker = input.readUTF();
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeLong(playerId.getMostSignificantBits());
		output.writeLong(playerId.getLeastSignificantBits());
		output.writeUTF(revoker);
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public UUID getPlayerId() {
		return playerId;
	}
}
