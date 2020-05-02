package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.AbstractPacket;

/**
* @deprecated From 1.8 release, it have been replaced by
*  @link com.elikill58.negativity.spigot.packets.event.PacketReceiveEvent : Called when the server received a packet from Player
*  @link com.elikill58.negativity.spigot.packets.event.PacketSentEvent : Called before the player sent the packet
*/
@Deprecated
public class NegativityPacketEvent extends Event {

	private final SpigotNegativityPlayer np;
	private final AbstractPacket packet;
	
	public NegativityPacketEvent(SpigotNegativityPlayer np, AbstractPacket packet) {
		this.np = np;
		this.packet = packet;
	}
	
	public SpigotNegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public Player getPlayer() {
		return np.getPlayer();
	}
	
	public AbstractPacket getPacket() {
		return packet;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	private static final HandlerList handlers = new HandlerList();
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
