package com.elikill58.negativity.spigot.packets.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.elikill58.negativity.spigot.packets.AbstractPacket;

public abstract class PacketEvent extends Event {
	
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
