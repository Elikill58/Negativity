package com.elikill58.negativity.api.packets.packet.playout;

import java.util.UUID;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutSpawnPlayer implements NPacketPlayOut {

	public int entityId, itemId;
	public UUID uuid;
	public double x, y, z;
	public float yaw, pitch;
	
	public NPacketPlayOutSpawnPlayer() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
	    this.entityId = serializer.readVarInt();
	    this.uuid = serializer.readUUID();
	    this.x = serializer.readInt();
	    this.y = serializer.readInt();
	    this.z = serializer.readInt();
	    this.yaw = serializer.readByte();
	    this.pitch = serializer.readByte();
	    this.itemId = serializer.readShort();
	    // TODO now read data watcher
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_PLAYER;
	}
}
