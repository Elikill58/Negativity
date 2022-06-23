package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;

/**
 * This packet is since the 1.17 (include). Before, it will never be called
 * 
 * @author Elikill58
 *
 */
public class NPacketPlayInGround extends NPacketPlayInFlying {
	
	public NPacketPlayInGround() {
		
	}

	public NPacketPlayInGround(boolean isGround) {
		super(0, 0, 0, 0, 0, isGround, false, false);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.GROUND;
	}
}
