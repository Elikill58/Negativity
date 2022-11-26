package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutSpawnEntityLiving implements NPacketPlayOut {

	public int entityId;
	public EntityType type;
	public double x, y, z;
	public double modX, modY, modZ;
	public float yaw, pitch;
	/**
	 * Don't know what is the behavior of this. It's named "aK" on spigot
	 */
	public float secondYaw;

	public NPacketPlayOutSpawnEntityLiving() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.entityId = serializer.readVarInt();
	    this.type = version.getOrCreateNamedVersion().getEntityType(serializer.readByte());
	    this.x = serializer.readInt();
	    this.y = serializer.readInt();
	    this.z = serializer.readInt();
	    this.yaw = serializer.readByte();
	    this.pitch = serializer.readByte();
	    this.secondYaw = serializer.readByte();
	    this.modX = serializer.readShort();
	    this.modY = serializer.readShort();
	    this.modZ = serializer.readShort();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_ENTITY_LIVING;
	}
}
