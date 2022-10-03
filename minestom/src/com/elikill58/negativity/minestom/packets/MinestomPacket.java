package com.elikill58.negativity.minestom.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;

public class MinestomPacket extends AbstractPacket {
	
	public MinestomPacket(NPacket nPacket, Object nmsPacket, Player p) {
		super(nmsPacket, nPacket, p);
	}

}
