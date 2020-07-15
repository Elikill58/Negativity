package com.elikill58.negativity.spigot.impl.events;

import java.util.HashMap;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.negativity.IPlayerPacketsClearEvent;
import com.elikill58.negativity.universal.PacketType;

public class PlayerPacketsClearEvent extends Event implements IPlayerPacketsClearEvent {

	private final Player p;
	private final NegativityPlayer np;
	private final HashMap<PacketType, Integer> packets;
	
	public PlayerPacketsClearEvent(Player p, NegativityPlayer np) {
		this.p = p;
		this.np = np;
		this.packets = new HashMap<>(np.PACKETS);
	}
	
	public Player getPlayer() {
		return p;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public HashMap<PacketType, Integer> getPackets(){
		return packets;
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
