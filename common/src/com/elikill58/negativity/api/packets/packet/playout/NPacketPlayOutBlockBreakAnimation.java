package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutBlockBreakAnimation implements NPacketPlayOut, LocatedPacket {

	public int x, y, z;
	public int entityId;
	public int destroyStage;
	
	public NPacketPlayOutBlockBreakAnimation() {
		
	}
	
	public NPacketPlayOutBlockBreakAnimation(BlockPosition pos, int entityId, int destroyStage) {
		this(pos.getX(), pos.getY(), pos.getZ(), entityId, destroyStage);
	}
	
	public NPacketPlayOutBlockBreakAnimation(int x, int y, int z, int entityId, int destroyStage) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.entityId = entityId;
		this.destroyStage = destroyStage;
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
		return PacketType.Server.BLOCK_BREAK_ANIMATION;
	}
}
