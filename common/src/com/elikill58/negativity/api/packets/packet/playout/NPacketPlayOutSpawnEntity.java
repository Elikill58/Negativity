package com.elikill58.negativity.api.packets.packet.playout;

import java.util.UUID;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutSpawnEntity implements NPacketPlayOut {

	public int entityId;
	/**
	 * This field appear in 1.9
	 * <br>
	 * For 1.8, a random one is generated to prevent NPE
	 */
	public UUID entityUUID;
	public EntityType type;
	public double x, y, z;
	public double modX, modY, modZ;
	public float yaw, pitch;

	public NPacketPlayOutSpawnEntity() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.entityId = serializer.readVarInt();
		int k;
		if(version.isNewerOrEquals(Version.V1_9)) {
			this.entityUUID = serializer.readUUID();
			this.type = version.getNamedVersion().getEntityType(serializer.readByte());
			this.x = serializer.readDouble();
			this.y = serializer.readDouble();
			this.z = serializer.readDouble();
			this.yaw = serializer.readByte() * 256.0F / 360.0F;
			this.pitch = serializer.readByte() * 256.0F / 360.0F;
			k = serializer.readVarInt();
		} else {
			this.entityUUID = UUID.randomUUID(); // no UUID
			this.type = version.getNamedVersion().getEntityType(serializer.readByte());
			this.x = ((double) serializer.readInt()) / 32;
			this.y = ((double) serializer.readInt()) / 32;
			this.z = ((double) serializer.readInt()) / 32;
			this.yaw = serializer.readByte() * 256.0F / 360.0F;
			this.pitch = serializer.readByte() * 256.0F / 360.0F;
			k = serializer.readInt();
		}
		if (k > 0) {
			this.modX = serializer.readShort();
			this.modY = serializer.readShort();
			this.modZ = serializer.readShort();
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_ENTITY;
	}
}
