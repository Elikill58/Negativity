package com.elikill58.negativity.api.packets.packet.playin;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;

public class NPacketPlayInUnset implements NPacketPlayIn, NPacketUnset {

	public String packetName;
	public PacketType cible;
	
	public NPacketPlayInUnset() {
		this.packetName = null;
	}
	
	public NPacketPlayInUnset(String packetName) {
		this.packetName = packetName;
		this.cible = PacketType.getType(packetName);
	}

	@Override
	public void setPacketTypeCible(PacketType type) {
		this.cible = type;
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
		return PacketType.Client.UNSET;
	}
}
