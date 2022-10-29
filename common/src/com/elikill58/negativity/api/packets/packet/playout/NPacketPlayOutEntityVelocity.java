package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutEntityVelocity implements NPacketPlayOut {

	public int entityId;
	public Vector vec;

	public NPacketPlayOutEntityVelocity() {
		
	}
	
	public NPacketPlayOutEntityVelocity(int entityId, int x, int y, int z) {
		this.entityId = entityId;
		this.vec = new Vector(x, y, z);
	}
	
	@Override
	public void read(PacketSerializer serializer) {
		this.entityId = serializer.readVarInt();
		this.vec = serializer.readVector(); // maybe should make read vector with short and not float
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY_VELOCITY;
	}
}
