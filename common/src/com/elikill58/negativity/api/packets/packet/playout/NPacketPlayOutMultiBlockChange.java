package com.elikill58.negativity.api.packets.packet.playout;

import java.util.HashMap;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutMultiBlockChange implements NPacketPlayOut {

	public long chunkX, chunkY, chunkZ;
	/**
	 * This field seems to appear since 1.16
	 */
	public boolean suppressLightUpdate;
	public HashMap<BlockPosition, Material> blockStates = new HashMap<>();

	public NPacketPlayOutMultiBlockChange() {

	}

	@Override
	public void read(PacketSerializer serializer, Version version) {
		if (version.isNewerOrEquals(Version.V1_16)) {
			BlockPosition pos = serializer.readChunkSectionPosition();
			this.chunkX = pos.getX();
			this.chunkY = pos.getY();
			this.chunkZ = pos.getZ();
			if(!version.isNewerOrEquals(Version.V1_20))
				this.suppressLightUpdate = serializer.readBoolean();
			int amount = serializer.readVarInt();
			for (int i = 0; i < amount; i++) {
				long val = serializer.readVarLong();
				long blockStateId = val >> 12;
				int x = (int) ((chunkX << 4) + (val >>> 8 & 0xF));
				int y = (int) ((chunkY << 4) + (val >>> 0 & 0xF));
				int z = (int) ((chunkZ << 4) + (val >>> 4 & 0xF));
				blockStates.put(new BlockPosition(x, y, z), version.getNamedVersion().getMaterial((int) blockStateId));
			}
		} else {
			this.chunkX = serializer.readInt();
			this.chunkZ = serializer.readInt();
			int amount = serializer.readVarInt();
			for (int i = 0; i < amount; i++) {
				blockStates.put(serializer.readBlockPositionShort(), version.getNamedVersion().getMaterial(serializer.readVarInt()));
			}
		}
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Server.MULTI_BLOCK_CHANGE;
	}

}
