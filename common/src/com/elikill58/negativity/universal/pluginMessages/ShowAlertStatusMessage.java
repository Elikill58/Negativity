package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ShowAlertStatusMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 9;

	private UUID uuid;
	private boolean showAlert;

	public ShowAlertStatusMessage() {
		this(UUID.fromString("00000000-0000-0000-0000-000000000000"), true);
	}

	public ShowAlertStatusMessage(UUID uuid, boolean showAlert) {
		this.uuid = uuid;
		this.showAlert = showAlert;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		uuid = new UUID(input.readLong(), input.readLong());
		showAlert = input.readBoolean();
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeLong(uuid.getMostSignificantBits());
		output.writeLong(uuid.getLeastSignificantBits());
		output.writeBoolean(showAlert);
	}

	public UUID getUUID() {
		return uuid;
	}
	
	public boolean isShowAlert() {
		return showAlert;
	}
}
