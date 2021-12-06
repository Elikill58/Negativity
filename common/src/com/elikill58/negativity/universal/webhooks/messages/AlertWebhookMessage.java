package com.elikill58.negativity.universal.webhooks.messages;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AlertWebhookMessage extends WebhookMessage {

	private int amount, reliability;
	private Cheat cheat;
	
	public AlertWebhookMessage(WebhookMessageType messageType, Player concerned, String sender, long date, int amount, int reliability, Cheat cheat) {
		super(messageType, concerned, sender, date);
		this.amount = amount;
		this.reliability = reliability;
		this.cheat = cheat;
	}
	
	@Override
	public AlertWebhookMessage combine(WebhookMessage msg) {
		if(!(msg instanceof AlertWebhookMessage))
			return null;
		AlertWebhookMessage o = (AlertWebhookMessage) msg;
		if(o.cheat == cheat && concerned.getUniqueId().equals(o.concerned.getUniqueId()) && messageType.equals(o.messageType))
			return new AlertWebhookMessage(messageType, concerned, sender, date, amount + o.amount, UniversalUtils.parseInPorcent(this.reliability + o.amount), cheat);
		return null;
	}

	@Override
	public boolean canCombine() {
		return true;
	}
	
	@Override
	public String applyPlaceHolders(String message) {
		return super.applyPlaceHolders(message).replace("%amount%", String.valueOf(amount)).replace("%reliability%", String.valueOf(reliability)).replace("%cheat%", cheat.getName());
	}
}
