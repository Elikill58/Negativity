package com.elikill58.negativity.spigot.packets.custom;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;

public class CustomPacket extends AbstractPacket {
	
	public CustomPacket(NPacket nPacket, Object nmsPacket, Player p) {
		super(nmsPacket, nPacket, p);
	}
}