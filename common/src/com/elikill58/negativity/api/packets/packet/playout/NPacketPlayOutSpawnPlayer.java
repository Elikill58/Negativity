package com.elikill58.negativity.api.packets.packet.playout;

import java.util.UUID;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutSpawnPlayer implements NPacketPlayOut {

	public int entityId;
	/**
	 * Warn: this field have been removed since 1.9
	 */
	public int itemId;
	public UUID uuid;
	public double x, y, z;
	public float yaw, pitch;
	
	public NPacketPlayOutSpawnPlayer() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
	    this.entityId = serializer.readVarInt();
	    this.uuid = serializer.readUUID();
	    if(version.isNewerOrEquals(Version.V1_9)) {
		    this.x = serializer.readDouble();
		    this.y = serializer.readDouble();
		    this.z = serializer.readDouble();
	    } else {
		    this.x = serializer.readInt() / 32;
		    this.y = serializer.readInt() / 32;
		    this.z = serializer.readInt() / 32;
	    }
	    this.yaw = serializer.readByte();
	    this.pitch = serializer.readByte();
	    if(!version.isNewerOrEquals(Version.V1_9))
	    	this.itemId = serializer.readShort();
	    // finally, data watcher until 1.14 included
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_PLAYER;
	}
}
