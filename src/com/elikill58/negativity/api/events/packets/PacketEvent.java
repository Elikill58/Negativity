package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.api.packets.AbstractPacket;

public abstract class PacketEvent implements Event {
	
	private final Player p;
	private final AbstractPacket packet;
	private final PacketSourceType source;
	
	public PacketEvent(PacketSourceType source, AbstractPacket packet, Player p) {
		this.source = source;
		this.packet = packet;
		this.p = p;
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public AbstractPacket getPacket() {
		return packet;
	}
	
	public PacketSourceType getPacketSourceType() {
		return source;
	}
    
    public enum PacketSourceType {
    	PROTOCOLLIB, CUSTOM;
    }
}
