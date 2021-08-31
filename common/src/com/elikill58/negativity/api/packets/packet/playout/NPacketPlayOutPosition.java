package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutPosition implements NPacketPlayOut {

	public double x;
	public double y;
	public double z;
	public float yaw;
	public float pitch;
	
	public NPacketPlayOutPosition() {
		
	}
	
	public NPacketPlayOutPosition(double x, double y, double z, float yaw, float pitch) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.POSITION;
	}
}
