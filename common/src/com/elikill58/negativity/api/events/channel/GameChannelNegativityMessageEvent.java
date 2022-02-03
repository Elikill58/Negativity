package com.elikill58.negativity.api.events.channel;

import com.elikill58.negativity.api.entity.Player;

public class GameChannelNegativityMessageEvent extends ChannelNegativityMessageEvent {

	public GameChannelNegativityMessageEvent(Player p, byte[] data) {
		super(p, data);
	}


}
