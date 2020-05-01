package com.elikill58.orebfuscator.packets.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.orebfuscator.packets.AbstractPacket;

public class PacketEvent extends Event {
	
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
	
    private static final HandlerList handlers = new HandlerList();
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public enum PacketSourceType {
    	PROTOCOLLIB, CUSTOM;
    }
}
