package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

/**
 * Block place packet. Sometimes named "UseItemOn".<br>
 * Some field are not present, such as:<br>
 * - sequence (int) : no use, no description<br>
 * - insideBlock (boolean) : no use, present in few versions<br>
 * 
 * @author Elikill58
 *
 */
public class NPacketPlayInBlockPlace implements NPacketPlayIn, LocatedPacket {

	public Hand hand;
	public BlockFace face;
	public BlockPosition pos;
	/**
	 * This field are not known yet.
	 * TODO find what are those field (present at least for 1.8)
	 */
	public float f1, f2, f3;
	
	public NPacketPlayInBlockPlace() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
	    this.pos = serializer.readBlockPosition();
	    this.face = BlockFace.getById(serializer.readUnsignedByte());
	    this.hand = Hand.MAIN;
	    serializer.readItemStack();
	    this.f1 = serializer.readUnsignedByte() / 16.0F;
	    this.f2 = serializer.readUnsignedByte() / 16.0F;
	    this.f3 = serializer.readUnsignedByte() / 16.0F;
	}

	@Override
	public double getX() {
		return pos.getX();
	}

	@Override
	public double getY() {
		return pos.getY();
	}

	@Override
	public double getZ() {
		return pos.getZ();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.BLOCK_PLACE;
	}
}
