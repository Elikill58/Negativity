package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;

public class PacketSendEvent extends PacketEvent implements CancellableEvent {

	public PacketSendEvent(PacketSourceType source, AbstractPacket packet, Player p) {
		super(source, packet, p);
	}

	@Override
	public boolean isCancelled() {
		return getPacket().isCancelled();
	}

	@Override
	public void setCancelled(boolean cancel) {
		getPacket().setCancelled(cancel);
	}
}
