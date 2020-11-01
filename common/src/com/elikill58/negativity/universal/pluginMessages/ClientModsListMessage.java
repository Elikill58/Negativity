package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientModsListMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 3;

	private Map<String, String> mods;

	public ClientModsListMessage() {
		this(Collections.emptyMap());
	}

	public ClientModsListMessage(Map<String, String> mods) {
		this.mods = mods;
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		int modsCount = input.readInt();
		Map<String, String> mods = new HashMap<>(modsCount);
		for (int i = 0; i < modsCount; i++) {
			mods.put(input.readUTF(), input.readUTF());
		}
		this.mods = Collections.unmodifiableMap(mods);
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		output.writeInt(mods.size());
		for (Map.Entry<String, String> entry : mods.entrySet()) {
			output.writeUTF(entry.getKey());
			output.writeUTF(entry.getValue());
		}
	}

	public Map<String, String> getMods() {
		return mods;
	}
}
