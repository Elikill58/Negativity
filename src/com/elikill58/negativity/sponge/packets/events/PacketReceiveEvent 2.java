package com.elikill58.negativity.sponge.packets.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

import com.elikill58.negativity.sponge.packets.AbstractPacket;

public class PacketReceiveEvent extends PacketEvent implements Cancellable {

	public PacketReceiveEvent(PacketSourceType source, AbstractPacket packet, Player p) {
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
