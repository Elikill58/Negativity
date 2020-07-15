package com.elikill58.negativity.api.events.negativity;

import java.util.HashMap;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.Event;
import com.elikill58.negativity.universal.PacketType;

public interface IPlayerPacketsClearEvent extends Event {

	public Player getPlayer();
	
	public NegativityPlayer getNegativityPlayer();
	
	public HashMap<PacketType, Integer> getPackets();

}
