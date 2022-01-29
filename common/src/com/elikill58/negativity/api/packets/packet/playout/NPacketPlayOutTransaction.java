package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutTransaction implements NPacketPlayOut {

	public int windowId;
	public short uid;
	/**
	 * Warn: this value isn't fully supported yet.
	 */
	public boolean c;

	public NPacketPlayOutTransaction() {
	}

	public NPacketPlayOutTransaction(int windowId, short uid, boolean c) {
		this.windowId = windowId;
		this.uid = uid;
		this.c = c;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.TRANSACTION;
	}

}
