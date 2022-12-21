package com.elikill58.negativity.api.events.channel;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;

public class ProxyChannelNegativityMessageEvent extends ChannelNegativityMessageEvent {
	
	public ProxyChannelNegativityMessageEvent(Player p, byte[] data) {
		super(p, data);
	}

	public ProxyChannelNegativityMessageEvent(Player p, NegativityMessage message) {
		super(p, message);
	}
}
