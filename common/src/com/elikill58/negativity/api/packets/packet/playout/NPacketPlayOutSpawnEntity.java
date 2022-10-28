package com.elikill58.negativity.api.packets.packet.playout;

import java.util.UUID;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutSpawnEntity implements NPacketPlayOut {

	public int entityId;
	public UUID entityUUID;
	public EntityType type;
	public double x, y, z;
	
	public NPacketPlayOutSpawnEntity(EntityType type, int entityId, UUID entityUUID, double x, double y, double z) {
		this.type = type;
		this.entityId = entityId;
		this.entityUUID = entityUUID;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public NPacketPlayOutSpawnEntity() {
		
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_ENTITY;
	}
}
