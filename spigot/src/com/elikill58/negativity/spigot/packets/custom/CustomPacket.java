package com.elikill58.negativity.spigot.packets.custom;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.universal.PacketType;

public class CustomPacket extends AbstractPacket {
	
	public CustomPacket(PacketType type, Object packet, Player p) {
		super(type, packet, p);
	}
}