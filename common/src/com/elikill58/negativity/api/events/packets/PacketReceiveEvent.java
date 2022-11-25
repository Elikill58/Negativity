package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.packets.packet.NPacket;

public class PacketReceiveEvent extends PacketEvent implements CancellableEvent {

	private boolean cancelled = false;
	
	public PacketReceiveEvent(NPacket packet, Player p) {
		super(packet, p);
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
