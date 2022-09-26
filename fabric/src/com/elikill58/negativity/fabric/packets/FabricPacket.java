package com.elikill58.negativity.fabric.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;

public class FabricPacket extends AbstractPacket {
	
	public FabricPacket(NPacket nPacket, Object nmsPacket, Player p) {
		super(nmsPacket, nPacket, p);
	}

}
