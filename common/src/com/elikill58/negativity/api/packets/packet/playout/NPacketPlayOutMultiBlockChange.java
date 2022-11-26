package com.elikill58.negativity.api.packets.packet.playout;

import java.util.HashMap;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutMultiBlockChange implements NPacketPlayOut {

	public long chunkX, chunkZ;
	public HashMap<BlockPosition, Material> blockStates = new HashMap<>();
	
	public NPacketPlayOutMultiBlockChange() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		int amount = serializer.readVarInt();
		for(int i = 0; i < amount; i++) {
			blockStates.put(serializer.readBlockPositionShort(), version.getOrCreateNamedVersion().getMaterial(serializer.readVarInt()));
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.MULTI_BLOCK_CHANGE;
	}

}
