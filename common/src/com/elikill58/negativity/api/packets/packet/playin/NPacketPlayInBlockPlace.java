package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

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
	public BlockFace direction;
	public int x, y, z;
	
	public NPacketPlayInBlockPlace() {
		
	}
	
	public NPacketPlayInBlockPlace(Hand hand, int x, int y, int z, BlockFace dir) {
		this.hand = hand;
		this.x = x;
		this.y = y;
		this.z = z;
		this.direction = dir;
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
