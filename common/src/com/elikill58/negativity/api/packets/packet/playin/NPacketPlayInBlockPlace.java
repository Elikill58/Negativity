package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInBlockPlace implements NPacketPlayIn {

	public BlockPosition position;
	public ItemStack item;
	public Vector vector;
	
	public NPacketPlayInBlockPlace() {
		
	}
	
	public NPacketPlayInBlockPlace(int x, int y, int z, ItemStack item, Vector vector) {
		this(new BlockPosition(x, y, z), item, vector);
	}
	
	public NPacketPlayInBlockPlace(BlockPosition position, ItemStack item, Vector vector) {
		this.position = position;
		this.item = item;
		this.vector = vector;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.BLOCK_PLACE;
	}
}
