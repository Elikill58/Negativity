package com.elikill58.negativity.api.block.data;

import java.util.HashMap;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class ChunkData {

	/**
	 * Will be always null until NBT are not done in {@link PacketSerializer#readNBTTag()}
	 */
	public Object heightmaps;
	public byte[] data;
	public HashMap<Material, BlockPosition> blockEntites = new HashMap<>();
	
	public ChunkData(PacketSerializer serializer, Version version) {
		this.heightmaps = serializer.readNBTTag();
		this.data = serializer.readByteArray();
		int amountEntites = serializer.readVarInt();
		for(int i = 0; i < amountEntites; i++) {
            byte xz = serializer.readByte();
            short y = serializer.readShort();
            int blockType = serializer.readVarInt();
            /*Object nbt = */serializer.readNBTTag();
            blockEntites.put(version.getOrCreateNamedVersion().getMaterial(blockType), new BlockPosition(xz >> 4, y, xz & 15));
		}
	}
}
