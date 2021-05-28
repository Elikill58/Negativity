package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInBlockPlace implements NPacketPlayIn {

	public BlockPosition position;
	public ItemStack item;
	public int face;
	public Vector vector;
	
	public NPacketPlayInBlockPlace() {
		
	}
	
	public NPacketPlayInBlockPlace(int x, int y, int z, ItemStack item, int face, Vector vector) {
		this(new BlockPosition(x, y, z), item, face, vector);
	}
	
	public NPacketPlayInBlockPlace(BlockPosition position, ItemStack item, int face, Vector vector) {
		this.position = position;
		this.item = item;
		this.face = face;
		this.vector = vector;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.BLOCK_PLACE;
	}
}
