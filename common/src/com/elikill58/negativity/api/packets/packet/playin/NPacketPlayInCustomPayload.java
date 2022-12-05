package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketType.Client;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.universal.Version;

public class NPacketPlayInCustomPayload implements NPacketPlayIn {

	public String channel;
	public byte[] data;
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		this.channel = serializer.readString();
		this.data = serializer.readByteArray();
	}

	@Override
	public void write(PacketSerializer serializer, Version version) {
		serializer.writeString(channel);
		serializer.writeBytes(data);
	}

	@Override
	public PacketType getPacketType() {
		return Client.CUSTOM_PAYLOAD;
	}
}
