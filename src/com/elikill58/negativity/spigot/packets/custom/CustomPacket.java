package com.elikill58.negativity.spigot.packets.custom;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketType;

public class CustomPacket extends AbstractPacket {
	
	public CustomPacket(PacketType type, Object packet, Player p) {
		super(type, packet, p);
	}
}