package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.packets.packet.NPacket;

public abstract class PacketEvent extends PlayerEvent {
	
	private final NPacket packet;
	
	public PacketEvent(NPacket packet, Player p) {
		super(p);
		this.packet = packet;
	}
	
	public boolean hasPlayer() {
		return getPlayer() != null;
	}
	
	public NPacket getPacket() {
		return packet;
	}
}
