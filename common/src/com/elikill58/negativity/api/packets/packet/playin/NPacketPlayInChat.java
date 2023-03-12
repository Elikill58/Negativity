package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInChat implements NPacketPlayIn {

	public String message;
	//public Instant time; // seems to be since 1.18
	
	public NPacketPlayInChat() {
		
	}

	@Override
	public void read(PacketSerializer serializer, Version v) {
		this.message = serializer.readString();
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.CHAT;
	}
}
