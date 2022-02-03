package com.elikill58.negativity.api.events.channel;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.PlayerEvent;

public class ChannelMessageEvent extends PlayerEvent {

	private final byte[] data;
	private final String channel;
	
	public ChannelMessageEvent(Player p, String channel, byte[] data) {
		super(p);
		this.channel = channel;
		this.data = data;
	}
	
	public String getChannel() {
		return channel;
	}

	public byte[] getData() {
		return data;
	}
}
