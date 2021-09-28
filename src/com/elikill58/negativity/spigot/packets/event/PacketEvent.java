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
	
	/**
	 * Get the player that send/receive the packet<br>
	 * WARN: it can be NULL ! Specially for handshake packet.
	 * 
	 * @return the player OR NULL
	 */
	public Player getPlayer() {
		return p;
	}
	
	/**
	 * Check if there is a player for this packet
	 * 
	 * @return true if player is defined
	 */
	public boolean hasPlayer() {
		return p != null;
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
