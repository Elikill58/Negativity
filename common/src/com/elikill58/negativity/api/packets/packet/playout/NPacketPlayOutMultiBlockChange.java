package com.elikill58.negativity.api.packets.packet.playout;

import java.util.HashMap;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;

public class NPacketPlayOutMultiBlockChange implements NPacketPlayOut {

	public long chunkX, chunkZ;
	public HashMap<BlockPosition, Long> blockStates = new HashMap<>();

	public NPacketPlayOutMultiBlockChange(long chunkX, long chunkZ, long[] blocks) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		for(long value : blocks) {
	        int x = (int) (value >> 38);
	        int y = (int) (value << 52 >> 52);
	        int z = (int) (value << 26 >> 38);
	        blockStates.put(new BlockPosition(x, y, z), null);
		}
		// blocks encoding : blockStateId << 12 | (blockLocalX << 8 | blockLocalZ << 4 | blockLocalY)
	}

	public NPacketPlayOutMultiBlockChange(long chunkX, long chunkZ, HashMap<BlockPosition, Long> blocks) {
		this.chunkX = chunkX;
		this.chunkZ = chunkZ;
		this.blockStates = blocks;
	}

	public NPacketPlayOutMultiBlockChange() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer) {
		this.chunkX = serializer.readInt();
		this.chunkZ = serializer.readInt();
		int amount = serializer.readVarInt();
		for(int i = 0; i < amount; i++) {
			blockStates.put(serializer.readBlockPosition(), (long) serializer.readVarInt());
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.Server.MULTI_BLOCK_CHANGE;
	}

}
