package com.elikill58.orebfuscator.packets.custom;

import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.AbstractPacket;
import com.elikill58.orebfuscator.packets.PacketType.AbstractPacketType;

public class CustomPacket extends AbstractPacket {
	
	public CustomPacket(AbstractPacketType type, Object packet, Player p) {
		super(type, packet, p);
	}
}