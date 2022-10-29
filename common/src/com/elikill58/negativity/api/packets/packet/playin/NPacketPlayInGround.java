package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;

/**
 * This packet is since the 1.17 (include). Before, it will never be called
 * 
 * @author Elikill58
 *
 */
public class NPacketPlayInGround extends NPacketPlayInFlying {
	
	public NPacketPlayInGround() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
		super.read(serializer); // only has ground value
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.GROUND;
	}
}
