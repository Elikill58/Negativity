package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

/**
 * For 1.16 and lower, this is the "PacketPlayInTransaction" packet.
 * 
 * @author Elikill58
 */
public class NPacketPlayInPong implements NPacketPlayIn {

	public long id;

	public NPacketPlayInPong() {
	}

	public NPacketPlayInPong(long id) {
		this.id = id;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.PONG;
	}

}
