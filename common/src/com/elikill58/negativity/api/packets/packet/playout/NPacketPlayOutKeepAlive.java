package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutKeepAlive implements NPacketPlayOut {

	public long time;
	
	public NPacketPlayOutKeepAlive() {
		
	}

	public NPacketPlayOutKeepAlive(long time) {
		this.time = time;
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.KEEP_ALIVE;
	}
}
