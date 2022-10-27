package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.packets.Packet;

public abstract class PacketEvent extends PlayerEvent {
	
	private final Packet packet;
	private final PacketSourceType source;
	
	public PacketEvent(PacketSourceType source, Packet packet, Player p) {
		super(p);
		this.source = source;
		this.packet = packet;
	}
	
	public boolean hasPlayer() {
		return getPlayer() != null;
	}
	
	public Packet getPacket() {
		return packet;
	}
	
	public PacketSourceType getPacketSourceType() {
		return source;
	}
    
    public enum PacketSourceType {
		PROTOCOLLIB, PACKETGATE, CUSTOM
	}
}
