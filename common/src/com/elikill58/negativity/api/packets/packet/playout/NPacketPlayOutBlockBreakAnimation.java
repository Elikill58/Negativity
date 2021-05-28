package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutBlockBreakAnimation implements NPacketPlayOut {

	public int entityId;
	public BlockPosition position;
	public int destroyStage;
	
	public NPacketPlayOutBlockBreakAnimation() {
		
	}
	
	public NPacketPlayOutBlockBreakAnimation(int x, int y, int z, int entityId, int destroyStage) {
		this(new BlockPosition(x, y, z), entityId, destroyStage);
	}
	
	public NPacketPlayOutBlockBreakAnimation(BlockPosition position, int entityId, int destroyStage) {
		this.position = position;
		this.entityId = entityId;
		this.destroyStage = destroyStage;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.BLOCK_BREAK_ANIMATION;
	}
}
