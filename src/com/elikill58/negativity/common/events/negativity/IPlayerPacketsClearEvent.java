package com.elikill58.negativity.common.events.negativity;

import java.util.HashMap;

import com.elikill58.negativity.common.NegativityPlayer;
import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.Event;
import com.elikill58.negativity.universal.PacketType;

public interface IPlayerPacketsClearEvent extends Event {

	public Player getPlayer();
	
	public NegativityPlayer getNegativityPlayer();
	
	public HashMap<PacketType, Integer> getPackets();

}
