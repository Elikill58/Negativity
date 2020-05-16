package com.elikill58.negativity.universal.pluginMessages;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.elikill58.negativity.universal.Minerate;
import com.elikill58.negativity.universal.NegativityAccount;

public class AccountUpdateMessage implements NegativityMessage {

	public static final byte MESSAGE_ID = 6;

	private NegativityAccount account;

	public AccountUpdateMessage() {
		super();
	}

	public AccountUpdateMessage(NegativityAccount account) {
		this.account = account;
	}

	@Override
	public void readFrom(DataInputStream input) throws IOException {
		UUID playerId = new UUID(input.readLong(), input.readLong());
		String playerName = input.readUTF();
		String language = input.readUTF();

		HashMap<Minerate.MinerateType, Integer> minerateMined = new HashMap<>();
		int minerateEntriesCount = input.readInt();
		for (int i = 0; i < minerateEntriesCount; i++) {
			Minerate.MinerateType type = Minerate.MinerateType.valueOf(input.readUTF());
			int value = input.readInt();
			minerateMined.put(type, value);
		}
		int minerateFullMined = input.readInt();
		Minerate minerate = new Minerate(minerateMined, minerateFullMined);

		int mostClicksPerSecond = input.readInt();

		Map<String, Integer> warns = new HashMap<>();
		int warnsEntriesCount = input.readInt();
		for (int i = 0; i < warnsEntriesCount; i++) {
			warns.put(input.readUTF(), input.readInt());
		}

		account = new NegativityAccount(playerId, playerName, language, minerate, mostClicksPerSecond, warns);
	}

	@Override
	public void writeTo(DataOutputStream output) throws IOException {
		UUID playerId = account.getPlayerId();
		output.writeLong(playerId.getMostSignificantBits());
		output.writeLong(playerId.getLeastSignificantBits());

		output.writeUTF(account.getPlayerName());
		output.writeUTF(account.getLang());

		Minerate minerate = account.getMinerate();
		Minerate.MinerateType[] allMinerateTypes = Minerate.MinerateType.values();
		output.writeInt(allMinerateTypes.length);
		for (Minerate.MinerateType type : allMinerateTypes) {
			output.writeUTF(type.name());
			output.writeInt(minerate.getMinerateType(type));
		}
		output.writeInt(minerate.getFullMined());

		output.writeInt(account.getMostClicksPerSecond());

		Map<String, Integer> warns = account.getAllWarns();
		output.writeInt(warns.size());
		for (Map.Entry<String, Integer> warnEntry : warns.entrySet()) {
			output.writeUTF(warnEntry.getKey());
			output.writeInt(warnEntry.getValue());
		}
	}

	@Override
	public byte messageId() {
		return MESSAGE_ID;
	}

	public NegativityAccount getAccount() {
		return account;
	}
}
