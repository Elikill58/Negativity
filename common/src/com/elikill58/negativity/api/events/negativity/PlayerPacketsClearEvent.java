package com.elikill58.negativity.api.events.negativity;

import java.util.HashMap;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;
import com.elikill58.negativity.api.packets.PacketType;

public class PlayerPacketsClearEvent extends PlayerEvent {

	private final NegativityPlayer np;
	private final HashMap<PacketType, Integer> packets;
	
	public PlayerPacketsClearEvent(Player p, NegativityPlayer np) {
		super(p);
		this.np = np;
		this.packets = new HashMap<>(np.packets);
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public HashMap<PacketType, Integer> getPackets(){
		return packets;
	}
}
