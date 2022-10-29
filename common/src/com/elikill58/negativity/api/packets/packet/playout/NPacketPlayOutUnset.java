package com.elikill58.negativity.api.packets.packet.playout;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;

public class NPacketPlayOutUnset implements NPacketPlayOut, NPacketUnset {

	public String packetName;
	public PacketType cible = PacketType.Server.UNSET;
	
	public NPacketPlayOutUnset() {
		this.packetName = null;
	}
	
	public NPacketPlayOutUnset(String packetName) {
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
		return PacketType.Server.UNSET;
	}
}
