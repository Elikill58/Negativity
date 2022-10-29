package com.elikill58.negativity.api.packets.packet.handshake;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Handshake;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;
import com.elikill58.negativity.universal.Adapter;

public class NPacketHandshakeInSetProtocol implements NPacketHandshake {

	public int protocol, port, nextState;
	public String hostname;
	
	public NPacketHandshakeInSetProtocol() {
		
	}
	
	@Override
	public void read(PacketSerializer serializer) {
		try {
		    this.protocol = serializer.readVarInt();
		    this.hostname = serializer.readString(32767);
		    this.port = serializer.readUnsignedShort();
		    this.nextState = serializer.readVarInt();
		} catch (Exception e) {
			Adapter.getAdapter().debug("Wrong handshake read: " + e.getMessage());
		}
	}
	
	@Override
	public PacketType getPacketType() {
		return Handshake.IS_SET_PROTOCOL;
	}

}
