package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutEntityTeleport implements NPacketPlayOut, LocatedPacket {

	public int entityId;
	public double x, y, z;
	public float yaw;
	public float pitch;
	public boolean onGround;
	
	public NPacketPlayOutEntityTeleport() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer) {
	    this.entityId = serializer.readVarInt();
	    this.x = serializer.readInt();
	    this.y = serializer.readInt();
	    this.z = serializer.readInt();
	    this.yaw = serializer.readByte();
	    this.pitch = serializer.readByte();
	    this.onGround = serializer.readBoolean();
	}
	
	@Override
	public double getX() {
		return x;
	}
	
	@Override
	public double getY() {
		return y;
	}
	
	@Override
	public double getZ() {
		return z;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY_TELEPORT;
	}
}
