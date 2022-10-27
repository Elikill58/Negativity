package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutBlockUpdate implements NPacketPlayOut {

	public NPacketPlayOutBlockUpdate() {
		
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.BLOCK_CHANGE;
	}

}
