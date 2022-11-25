package com.elikill58.negativity.api.events.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.CancellableEvent;
import com.elikill58.negativity.api.packets.packet.NPacket;

/**
 * This event is called directly when the packet is received.
 * <p>
 * For ping-compensated packets, you can use {@link PacketReceiveEvent}
 * 
 * @author Elikill58
 *
 */
public class PrePacketReceiveEvent extends PacketEvent implements CancellableEvent {

	private boolean cancelled = false;
	
	public PrePacketReceiveEvent(NPacket packet, Player p) {
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
