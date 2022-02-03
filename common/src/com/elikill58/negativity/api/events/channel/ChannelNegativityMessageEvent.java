package com.elikill58.negativity.api.events.channel;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;

public class ChannelNegativityMessageEvent extends ChannelMessageEvent {

	private NegativityMessage message;
	
	public ChannelNegativityMessageEvent(Player p, byte[] data) {
		super(p, NegativityMessagesManager.CHANNEL_ID, data);
		try {
			this.message = NegativityMessagesManager.readMessage(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public NegativityMessage getMessage() {
		return message;
	}
}
