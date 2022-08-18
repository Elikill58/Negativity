package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.warn.Warn;

public class ProxyExecuteWarnMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 10;

	private Warn warn;

	public ProxyExecuteWarnMessage() {
	}

	public ProxyExecuteWarnMessage(Warn warn) {
		this.warn = warn;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		this.warn = new Warn(input.readInt(), new UUID(input.readLong(), input.readLong()), input.readUTF(), input.readUTF(),
				SanctionnerType.valueOf(input.readUTF()), input.readUTF(), input.readLong(), input.readBoolean(),
				input.readLong(), input.readUTF());
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeInt(warn.getId());
		output.writeLong(warn.getPlayerId().getMostSignificantBits());
		output.writeLong(warn.getPlayerId().getLeastSignificantBits());
		output.writeUTF(warn.getReason());
		output.writeUTF(warn.getWarnedBy());
		output.writeUTF(warn.getSanctionnerType().name());
		output.writeUTF(warn.getIp());
		output.writeLong(warn.getExecutionTime());
		output.writeBoolean(warn.isActive());
		output.writeLong(warn.getRevocationTime());
		output.writeUTF(warn.getRevocationBy());
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public Warn getWarn() {
		return warn;
	}
}
