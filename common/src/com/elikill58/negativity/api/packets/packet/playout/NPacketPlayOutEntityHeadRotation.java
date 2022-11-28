package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutEntityHeadRotation implements NPacketPlayOut {

	public int entityId;
	public float yaw;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.entityId = serializer.readVarInt();
		this.yaw = (serializer.readByte() * 360f) / 256f;
	}

	@Override
	public PacketType getPacketType() {
		return Server.ENTITY_HEAD_ROTATION;
	}
}
