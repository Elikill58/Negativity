package com.elikill58.negativity.spigot.packets.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.packets.AbstractPacket;

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

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
