package com.elikill58.negativity.api.block.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class LightData {
	
	public boolean trustEdge;
	public long[] skyMask, blockMask, emptySkyMask, emptyBlockMask;
    public List<byte[]> skyLight = new ArrayList<>(), blockLight = new ArrayList<>();
	
	public LightData(PacketSerializer serializer, Version version) {
		this.trustEdge = serializer.readBoolean();
		this.skyMask = serializer.readLongArray(2048);
		this.blockMask = serializer.readLongArray(2048);
		this.emptySkyMask = serializer.readLongArray(2048);
		this.emptyBlockMask = serializer.readLongArray(2048);

		int skyLength = serializer.readVarInt();
		for(int i = 0; i < skyLength; i++)
			skyLight.add(serializer.readByteArray(2048));
		
		int blockLength = serializer.readVarInt();
		for(int i = 0; i < blockLength; i++)
			blockLight.add(serializer.readByteArray(2048));
	}

}
