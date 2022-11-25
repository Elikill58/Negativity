package com.elikill58.negativity.api.packets.packet.playin;

import java.time.Instant;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInChat implements NPacketPlayIn {

	public String message;
	// fields only for new versions :
	public Instant time;
	
	public NPacketPlayInChat() {
		
	}

	@Override
	public void read(PacketSerializer serializer, Version v) {
		if(v.isNewerOrEquals(Version.V1_18)) {
			this.message = serializer.readString(256);
			this.time = serializer.readInstant();
		} else
			this.message = serializer.readString(100);
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Client.CHAT;
	}
}
