package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutEntity implements NPacketPlayOut {

	public int entityId;
	public Vector vec;
	
	public NPacketPlayOutEntity() {
		
	}
	
	public NPacketPlayOutEntity(int entityId, double vecX, double vecY, double vecZ) {
		this.entityId = entityId;
		this.vec = new Vector(vecX, vecY, vecZ);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY;
	}
}
