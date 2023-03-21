package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutBlockChange implements NPacketPlayOut {

	public BlockPosition pos;
	public long stateId;
	public Material type;
	
	public NPacketPlayOutBlockChange(BlockPosition pos, long state) {
		this.pos = pos;
		this.stateId = state;
	}
	
	public NPacketPlayOutBlockChange() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.pos = serializer.readBlockPosition(version);
		/*this.stateId = serializer.readVarInt();
		this.type = version.getNamedVersion().getMaterial((int) (stateId));*/
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.BLOCK_CHANGE;
	}

}
