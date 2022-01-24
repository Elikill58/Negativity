package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.detections.Cheat;

public class AlertMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 0;

	private UUID playerId;
	private String cheat;
	private int reliability;
	private int ping;
	private Cheat.@Nullable CheatHover hoverInfo;
	private int alertsCount;

	public AlertMessage() {
		this(UUID.fromString("00000000-0000-0000-0000-000000000000"), "", -1, -1, null, 0);
	}

	public AlertMessage(UUID playerId, String cheat, int reliability, int ping, Cheat.@Nullable CheatHover hoverInfo, int alertsCount) {
		this.playerId = playerId;
		this.cheat = cheat;
		this.reliability = reliability;
		this.ping = ping;
		this.hoverInfo = hoverInfo;
		this.alertsCount = alertsCount;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		playerId = UUID.fromString(input.readUTF());
		cheat = input.readUTF();
		reliability = input.readInt();
		ping = input.readInt();
		hoverInfo = readCheatHover(input);
		alertsCount = input.readInt();
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeUTF(playerId.toString());
		output.writeUTF(cheat);
		output.writeInt(reliability);
		output.writeInt(ping);
		writeCheatHover(hoverInfo, output);
		output.writeInt(alertsCount);
	}

	private Cheat.@Nullable CheatHover readCheatHover(DataInputStream input) throws IOException {
		String key = input.readUTF();
		if (key.isEmpty()) {
			return null;
		}

		List<String> args = new ArrayList<>();
		int argsCount = input.readInt();
		for (int i = 0; i < argsCount; i++) {
			args.add(input.readUTF());
		}
		return new Cheat.CheatHover(key, args.toArray());
	}

	private void writeCheatHover(Cheat.@Nullable CheatHover hover, DataOutputStream output) throws IOException {
		if (hover == null) {
			output.writeUTF("");
			return;
		}

		output.writeUTF(hover.getKey());
		Object[] args = hover.getPlaceholders();
		output.writeInt(args.length);
		for (Object arg : args) {
			output.writeUTF(String.valueOf(arg));
		}
	}

	public UUID getPlayerUUID() {
		return playerId;
	}

	public String getCheat() {
		return cheat;
	}

	public int getReliability() {
		return reliability;
	}

	public int getPing() {
		return ping;
	}

	public Cheat.@Nullable CheatHover getHoverInfo() {
		return hoverInfo;
	}

	public int getAlertsCount() {
		return alertsCount;
	}

	public boolean isMultiple() {
		return alertsCount > 1;
	}
}
