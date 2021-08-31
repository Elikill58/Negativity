package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutEntityTeleport implements NPacketPlayOut {

	public int entityId;
	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	public boolean onGround;
	
	public NPacketPlayOutEntityTeleport() {
		
	}
	
	public NPacketPlayOutEntityTeleport(int entityId, double x, double y, double z, float yaw, float pitch, boolean onGround) {
		this.entityId = entityId;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.ENTITY_TELEPORT;
	}
}
