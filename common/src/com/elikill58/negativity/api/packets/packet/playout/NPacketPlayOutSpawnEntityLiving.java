package com.elikill58.negativity.api.packets.packet.playout;

import java.util.UUID;

import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutSpawnEntityLiving implements NPacketPlayOut {

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
	/**
	 * Don't know what is the behavior of this. It's named "aK" on spigot
	 */
	public float secondYaw;

	public NPacketPlayOutSpawnEntityLiving() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.entityId = serializer.readVarInt();
		if(version.isNewerOrEquals(Version.V1_9)) {
			this.entityUUID = serializer.readUUID();
			this.type = version.getOrCreateNamedVersion().getEntityType(serializer.readByte());
			this.x = serializer.readDouble();
			this.y = serializer.readDouble();
			this.z = serializer.readDouble();
			this.yaw = serializer.readByte() * 256.0F / 360.0F;
			this.pitch = serializer.readByte() * 256.0F / 360.0F;
		    this.secondYaw = serializer.readByte() * 256.0F / 360.0F;
		    this.modX = serializer.readShort();
		    this.modY = serializer.readShort();
		    this.modZ = serializer.readShort();
		} else {
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
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_ENTITY_LIVING;
	}
}
