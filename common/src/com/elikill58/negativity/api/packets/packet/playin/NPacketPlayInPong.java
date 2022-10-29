package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
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

	@Override
	public void read(PacketSerializer serializer) {
	    this.id = serializer.readByte();
	    // 1.8 fields
	    serializer.readShort();
	    serializer.readByte();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.PONG;
	}

}
