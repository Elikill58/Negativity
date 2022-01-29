package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInTransaction implements NPacketPlayIn {

	public int windowId;
	public short uid;
	/**
	 * Warn: this value isn't fully supported yet.
	 */
	public boolean c;

	public NPacketPlayInTransaction() {
	}

	public NPacketPlayInTransaction(int windowId, short uid, boolean c) {
		this.windowId = windowId;
		this.uid = uid;
		this.c = c;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.TRANSACTION;
	}

}
