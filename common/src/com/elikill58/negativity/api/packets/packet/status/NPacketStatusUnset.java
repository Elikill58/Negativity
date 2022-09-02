package com.elikill58.negativity.api.packets.packet.status;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacketStatus;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;

public class NPacketStatusUnset implements NPacketStatus, NPacketUnset {

	public String packetName;
	public PacketType cible;
	
	public NPacketStatusUnset() {
		this.packetName = null;
	}
	
	public NPacketStatusUnset(String packetName) {
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
		return PacketType.Status.UNSET;
	}
}
