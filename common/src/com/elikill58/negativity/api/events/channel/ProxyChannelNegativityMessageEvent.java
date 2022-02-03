package com.elikill58.negativity.api.events.channel;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;

public class ProxyChannelNegativityMessageEvent extends ChannelNegativityMessageEvent {

	private final boolean shouldBeSendToMultiProxy;
	
	public ProxyChannelNegativityMessageEvent(Player p, byte[] data, boolean shouldBeSendToMultiProxy) {
		super(p, data);
		this.shouldBeSendToMultiProxy = shouldBeSendToMultiProxy;
	}

	public ProxyChannelNegativityMessageEvent(Player p, NegativityMessage message, boolean shouldBeSendToMultiProxy) {
		super(p, message);
		this.shouldBeSendToMultiProxy = shouldBeSendToMultiProxy;
	}

	public boolean isShouldBeSendToMultiProxy() {
		return shouldBeSendToMultiProxy;
	}
}
