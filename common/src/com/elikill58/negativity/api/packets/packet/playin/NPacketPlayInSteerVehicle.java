package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInSteerVehicle implements NPacketPlayIn {

	public float sideways = 0, forward = 0;
	public boolean jumping = false, sneaking = false;
	
	public NPacketPlayInSteerVehicle() {
		
	}
	
	public NPacketPlayInSteerVehicle(float sideways, float forward, boolean jumping, boolean sneaking) {
		this.sideways = sideways;
		this.forward = forward;
		this.jumping = jumping;
		this.sneaking = sneaking;
	}
	
	@Override
	public void read(PacketSerializer serializer) {
	    this.sideways = serializer.readFloat();
	    this.forward = serializer.readFloat();
	    byte b = serializer.readByte();
	    this.jumping = ((b & 0x1) > 0);
	    this.sneaking = ((b & 0x2) > 0);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.STEER_VEHICLE;
	}
}
