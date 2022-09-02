package com.elikill58.negativity.api.packets.packet.login;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketLogin;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;

public class NPacketLoginUnset implements NPacketLogin, NPacketUnset {

	public String packetName;
	public PacketType cible;
	
	public NPacketLoginUnset() {
		this.packetName = null;
	}
	
	public NPacketLoginUnset(String packetName) {
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
		return PacketType.Login.UNSET;
	}
}
