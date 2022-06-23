package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

/**
 * This packet appear in 1.9
 * 
 * @author Elikill58
 *
 */
public class NPacketPlayInTeleportAccept implements NPacketPlayIn {

	public int teleportId = 0;
	
	public NPacketPlayInTeleportAccept() {
		
	}
	
	public NPacketPlayInTeleportAccept(int teleportId) {
		this.teleportId = teleportId;
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.TELEPORT_ACCEPT;
	}
}
