package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

/**
 * For 1.16 and lower, this is the "PacketPlayOutTransaction" packet.
 * 
 * @author Elikill58
 */
public class NPacketPlayOutPing implements NPacketPlayOut {

	public long id;

	public NPacketPlayOutPing() {
	}

	public NPacketPlayOutPing(long id) {
		this.id = id;
	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.id = serializer.readUnsignedByte();
		// 1.8 fields
		// serializer.readShort();
		// serializer.readBoolean();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.PING;
	}

}
