package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.utils.LocationUtils.Direction;

public class NPacketPlayInUseItem implements NPacketPlayIn, LocatedPacket {

	public Direction direction;
	public int x, y, z;
	/**
	 * WARN: this value seems to doesn't exist in some version such as 1.9.
	 * <br> 
	 * So, it will be 0.
	 */
	public long timestamp;
	
	public NPacketPlayInUseItem() {
		
	}
	
	public NPacketPlayInUseItem(int x, int y, int z, Direction dir, long timestamp) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = dir;
		this.timestamp = timestamp;
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
		return PacketType.Client.USE_ITEM;
	}
}
