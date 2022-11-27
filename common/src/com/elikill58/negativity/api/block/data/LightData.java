package com.elikill58.negativity.api.block.data;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

public class LightData {
	
	public boolean trustEdge;
	public long[] skyMask, blockMask, emptySkyMask, emptyBlockMask;
    public List<byte[]> skyLight, blockLight;
	
	public LightData(PacketSerializer serializer, Version version) {
		this.trustEdge = serializer.readBoolean();
		this.skyMask = serializer.readLongArray();
		this.blockMask = serializer.readLongArray();
		this.emptySkyMask = serializer.readLongArray();
		this.emptyBlockMask = serializer.readLongArray();

		this.skyLight = new ArrayList<>();
		int skyLength = serializer.readVarInt();
		for(int i = 0; i < skyLength; i++)
			skyLight.add(serializer.readByteArray());
		
		this.blockLight = new ArrayList<>();
		int blockLength = serializer.readVarInt();
		for(int i = 0; i < blockLength; i++)
			blockLight.add(serializer.readByteArray());
	}

}
