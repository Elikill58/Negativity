package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;

public class NPacketPlayInChat implements NPacketPlayIn {

	public String message;
	
	public NPacketPlayInChat() {
		
	}

	@Override
	public void read(PacketSerializer serializer) {
		this.message = serializer.readString(100);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.CHAT;
	}
}
