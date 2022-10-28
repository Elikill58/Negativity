package com.elikill58.negativity.api.packets.packet.playout;

import java.util.UUID;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutSpawnPlayer implements NPacketPlayOut {

	public int id;
	public UUID uuid;
	public double x, y, z;
	
	public NPacketPlayOutSpawnPlayer(int id, UUID uuid, double x, double y, double z) {
		this.id = id;
		this.uuid = uuid;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public NPacketPlayOutSpawnPlayer() {
		
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.SPAWN_PLAYER;
	}
}
