package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInUseItem implements NPacketPlayIn {

	public BlockPosition pos;
	public Hand hand;
	public BlockFace face;
	public float f1, f2, f3;
	
	public NPacketPlayInUseItem() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
	    this.pos = serializer.readBlockPosition();
	    this.face = BlockFace.getById(serializer.readVarInt());
	    this.hand = serializer.getEnum(Hand.class);
	    this.f1 = serializer.readUnsignedByte() / 16.0F;
	    this.f2 = serializer.readUnsignedByte() / 16.0F;
	    this.f3 = serializer.readUnsignedByte() / 16.0F;
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Client.USE_ITEM;
	}
}
