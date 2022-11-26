package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutEntity implements NPacketPlayOut {

	public int entityId;
	public double deltaX, deltaY, deltaZ;
	public float yaw, pitch;
	public boolean isGround;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.entityId = serializer.readVarInt();
	}

	@Override
	public PacketType getPacketType() {
		return Server.ENTITY;
	}
}
