package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutBlockChange implements NPacketPlayOut {

	public BlockPosition pos;
	public long stateId;
	
	public NPacketPlayOutBlockChange(BlockPosition pos, long state) {
		this.pos = pos;
		this.stateId = state;
	}
	
	public NPacketPlayOutBlockChange() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer) {
		this.pos = serializer.readBlockPosition();
		this.stateId = serializer.readVarInt();
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.BLOCK_CHANGE;
	}

}
