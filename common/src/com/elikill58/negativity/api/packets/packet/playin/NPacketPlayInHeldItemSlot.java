package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInHeldItemSlot implements NPacketPlayIn {

	public int slot;

	public NPacketPlayInHeldItemSlot() {
		
	}

	public NPacketPlayInHeldItemSlot(int slot) {
		this.slot = slot;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.HELD_ITEM_SLOT;
	}
}
