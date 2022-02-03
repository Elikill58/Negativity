package com.elikill58.negativity.api.events.channel;

import com.elikill58.negativity.api.entity.Player;

public class ProxyChannelNegativityMessageEvent extends ChannelNegativityMessageEvent {

	public ProxyChannelNegativityMessageEvent(Player p, byte[] data) {
		super(p, data);
	}

}
