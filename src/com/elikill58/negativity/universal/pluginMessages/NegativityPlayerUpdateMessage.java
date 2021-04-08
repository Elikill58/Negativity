package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;

public class NegativityPlayerUpdateMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 7;

	private NegativityPlayer np;

	public NegativityPlayerUpdateMessage() {
		super();
	}
	
	public NegativityPlayerUpdateMessage(NegativityPlayer np) {
		super();
		this.np = np;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		UUID playerId = new UUID(input.readLong(), input.readLong());
		
		NegativityPlayer np = Adapter.getAdapter().getNegativityPlayer(playerId);
		np.setShowAlert(input.readBoolean());
		np.setMcLeaks(input.readBoolean());
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		UUID playerId = np.getUUID();
		output.writeLong(playerId.getMostSignificantBits());
		output.writeLong(playerId.getLeastSignificantBits());

		output.writeBoolean(np.isShowAlert());
		output.writeBoolean(np.isMcLeaks());
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}
}
