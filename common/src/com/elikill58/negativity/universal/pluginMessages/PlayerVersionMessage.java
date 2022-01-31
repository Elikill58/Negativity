package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.elikill58.negativity.universal.Version;

public class PlayerVersionMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 8;

	private UUID uuid;
	private Version version;

	public PlayerVersionMessage() {
		super();
	}

	public PlayerVersionMessage(UUID uuid, Version version) {
		this.uuid = uuid;
		this.version = version;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		uuid = new UUID(input.readLong(), input.readLong());
		version = Version.getVersionByProtocolID(input.readInt());
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeLong(uuid.getMostSignificantBits());
		output.writeLong(uuid.getLeastSignificantBits());
		
		if(version != null)
			output.writeInt(version.getFirstProtocolNumber());
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public Version getVersion() {
		return version;
	}
}
