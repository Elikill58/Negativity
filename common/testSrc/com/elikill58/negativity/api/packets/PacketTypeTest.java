package com.elikill58.negativity.api.packets;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.elikill58.negativity.api.packets.packet.NPacket;

public class PacketTypeTest {

	@Test
	public void checkPacketType() {
		for(PacketType types : PacketType.values()) {
			NPacket npacket = types.createNewPacket();
			if(!npacket.getPacketType().isUnset()) {
				assertEquals(types, npacket.getPacketType(), "PacketType " + types.getPacketName() + " create packet with wrong packet type " + npacket.getPacketType().getPacketName());
			}
		}
	}
}
