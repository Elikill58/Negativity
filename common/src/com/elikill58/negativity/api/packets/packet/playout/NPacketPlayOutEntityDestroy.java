package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Server;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayOutEntityDestroy implements NPacketPlayOut {

	public int[] entityIds;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		if(version.equals(Version.V1_17)) {
			this.entityIds = new int[1];
			this.entityIds[0] = serializer.readVarInt();
		} else {
			this.entityIds = new int[serializer.readVarInt()];
			for(int i = 0; i < entityIds.length; i++)
				entityIds[i] = serializer.readVarInt();
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return Server.ENTITY_DESTROY;
	}
}
