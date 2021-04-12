package com.elikill58.negativity.spigot.packets.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.packets.AbstractPacket;

public class PacketSendEvent extends PacketEvent implements Cancellable {

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

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
