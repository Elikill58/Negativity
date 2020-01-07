package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AlertMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 0;

	private String playername;
	private String cheat;
	private int reliability;
	private int ping;
	private String hoverInfo;
	private boolean isMultiple;

	public AlertMessage() {
		this("", "", -1, -1, "", false);
	}

	public AlertMessage(String playername, String cheat, int reliability, int ping, String hoverInfo, boolean isMultiple) {
		this.playername = playername;
		this.cheat = cheat;
		this.reliability = reliability;
		this.ping = ping;
		this.hoverInfo = hoverInfo;
		this.isMultiple = isMultiple;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		playername = input.readUTF();
		cheat = input.readUTF();
		reliability = input.readInt();
		ping = input.readInt();
		hoverInfo = input.readUTF();
		isMultiple = input.readBoolean();
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeUTF(playername);
		output.writeUTF(cheat);
		output.writeInt(reliability);
		output.writeInt(ping);
		output.writeUTF(hoverInfo);
		output.writeBoolean(isMultiple);
	}

	public String getPlayername() {
		return playername;
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

	public String getHoverInfo() {
		return hoverInfo;
	}

	public boolean isMultiple() {
		return isMultiple;
	}
}
