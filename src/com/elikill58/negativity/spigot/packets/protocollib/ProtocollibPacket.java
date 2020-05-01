package com.elikill58.negativity.spigot.packets.protocollib;

import org.bukkit.entity.Player;

import com.comphenix.protocol.events.PacketEvent;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketType.AbstractPacketType;

public class ProtocollibPacket extends AbstractPacket {
	
	private PacketEvent event;
	
	public ProtocollibPacket(AbstractPacketType type, Object packet, Player p, PacketEvent event) {
		super(type, packet, p);
		this.event = event;
	}
	
	public PacketEvent getProtocollibEvent() {
		return event;
	}
}
