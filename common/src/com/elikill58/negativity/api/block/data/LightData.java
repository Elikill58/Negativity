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
		int maxSize = Integer.MAX_VALUE; // should be 2048 but idk from which version
		this.skyMask = serializer.readLongArray(maxSize);
		this.blockMask = serializer.readLongArray(maxSize);
		this.emptySkyMask = serializer.readLongArray(maxSize);
		this.emptyBlockMask = serializer.readLongArray(maxSize);

		int skyLength = serializer.readVarInt();
		for(int i = 0; i < skyLength; i++)
			skyLight.add(serializer.readByteArray(maxSize));
		
		int blockLength = serializer.readVarInt();
		for(int i = 0; i < blockLength; i++)
			blockLight.add(serializer.readByteArray(maxSize));
	}

}
