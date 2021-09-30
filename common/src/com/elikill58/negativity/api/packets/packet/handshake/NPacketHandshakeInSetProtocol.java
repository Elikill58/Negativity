package com.elikill58.negativity.api.packets.packet.handshake;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Handshake;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;

public class NPacketHandshakeInSetProtocol implements NPacketHandshake {

	public int procotol, port;
	public String hostname;
	
	public NPacketHandshakeInSetProtocol() {
		
	}
	
	public NPacketHandshakeInSetProtocol(int protocol, String hostname, int port) {
		this.procotol = protocol;
		this.hostname = hostname;
		this.port = port;
	}
	
	@Override
	public PacketType getPacketType() {
		return Handshake.IS_SET_PROTOCOL;
	}

}
