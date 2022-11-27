package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.packets.LocatedPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutBlockBreakAnimation implements NPacketPlayOut, LocatedPacket {

	public int entityId;
	public BlockPosition pos;
	public int destroyStage;
	
	public NPacketPlayOutBlockBreakAnimation() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
	    this.entityId = serializer.readVarInt();
	    this.pos = serializer.readBlockPosition(version);
	    this.destroyStage = serializer.readUnsignedByte();
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
		return PacketType.Server.BLOCK_BREAK_ANIMATION;
	}
}
