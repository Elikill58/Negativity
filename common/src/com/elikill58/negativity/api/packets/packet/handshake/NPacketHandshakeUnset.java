package com.elikill58.negativity.api.packets.packet.handshake;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;
import com.elikill58.negativity.universal.Version;

public class NPacketHandshakeUnset implements NPacketHandshake, NPacketUnset {

	public String packetName;
	public PacketType cible;
	
	public NPacketHandshakeUnset() {
		this.packetName = null;
	}
	
	public NPacketHandshakeUnset(String packetName) {
		this.packetName = packetName;
		this.cible = PacketType.getType(packetName);
	}
	
	@Override
	public void read(PacketSerializer serializer, Version version) {
		
	}

	@Override
	public void setPacketTypeCible(PacketType type) {
		this.cible = type;
		this.packetName = type.getPacketName();
	}

	@Override
	public PacketType getPacketTypeCible() {
		return cible;
	}
	
	@Override
	public void setPacketName(String name) {
		this.packetName = name;
	}
	
	@Override
	public String getPacketName() {
		return packetName;
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.Handshake.UNSET;
	}
}
