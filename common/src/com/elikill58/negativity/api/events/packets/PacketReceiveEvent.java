package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.AbstractPacket;

public class PacketReceiveEvent extends PacketEvent {

	public PacketReceiveEvent(PacketSourceType source, AbstractPacket packet, Player p) {
		super(source, packet, p);
	}

	public boolean isCancelled() {
		return getPacket().isCancelled();
	}

	public void setCancelled(boolean cancel) {
		getPacket().setCancelled(cancel);
	}
}
