package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.packet.NPacket;

/**
 * For cancelling, use {@link PacketPreReceiveEvent}
 * 
 * @author Elikill58
 */
public class PacketReceiveEvent extends PacketEvent {
	
	public PacketReceiveEvent(NPacket packet, Player p) {
		super(packet, p);
	}
	
	public void setCancelled(boolean sanatize) {
		
	}
}
