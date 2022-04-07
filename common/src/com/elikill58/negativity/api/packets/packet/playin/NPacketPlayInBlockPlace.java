package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInBlockPlace implements NPacketPlayIn, LocatedPacket {

	public int x, y, z;
	public ItemStack item;
	public Vector vector;
	
	public NPacketPlayInBlockPlace() {
		
	}
	
	public NPacketPlayInBlockPlace(int x, int y, int z, ItemStack item, Vector vector) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.item = item;
		this.vector = vector;
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
		return PacketType.Client.BLOCK_PLACE;
	}
}
