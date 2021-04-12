package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;

public class ProxyExecuteBanMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 4;

	private Ban ban;

	public ProxyExecuteBanMessage() {
	}

	public ProxyExecuteBanMessage(Ban ban) {
		this.ban = ban;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		this.ban = new Ban(
				new UUID(input.readLong(), input.readLong()),
				input.readUTF(),
				input.readUTF(),
				BanType.valueOf(input.readUTF()),
				input.readLong(),
				input.readBoolean() ? input.readUTF() : null,
				BanStatus.valueOf(input.readUTF()),
				input.readLong(),
				input.readLong()
		);
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeLong(ban.getPlayerId().getMostSignificantBits());
		output.writeLong(ban.getPlayerId().getLeastSignificantBits());
		output.writeUTF(ban.getReason());
		output.writeUTF(ban.getBannedBy());
		output.writeUTF(ban.getBanType().name());
		output.writeLong(ban.getExpirationTime());
		if (ban.getCheatName() != null) {
			output.writeBoolean(true);
			output.writeUTF(ban.getCheatName());
		} else {
			output.writeBoolean(false);
		}
		output.writeUTF(ban.getStatus().name());
		output.writeLong(ban.getExecutionTime());
		output.writeLong(ban.getRevocationTime());
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public Ban getBan() {
		return ban;
	}
}
