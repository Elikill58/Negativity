package com.elikill58.negativity.universal.webhooks.messages;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AlertWebhookMessage extends WebhookMessage {

	private final long amount;
	private final int reliability;
	private final Cheat cheat;
	
	public AlertWebhookMessage(WebhookMessageType messageType, Player concerned, String sender, long date, long amount, int reliability, Cheat cheat) {
		super(messageType, concerned, sender, date);
		this.amount = amount;
		this.reliability = reliability;
		this.cheat = cheat;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public Cheat getCheat() {
		return cheat;
	}
	
	public int getReliability() {
		return reliability;
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
	public int compareTo(WebhookMessage o) {
		if(!(o instanceof AlertWebhookMessage))
			return -1;
		AlertWebhookMessage m = (AlertWebhookMessage) o;
		if(getReliability() != m.getReliability()) {
			return m.getReliability() - getReliability();
		}
		if(getAmount() != m.getAmount()) {
			return (int) (m.getAmount() - getAmount());
		}
		return super.compareTo(o);
	}
	
	@Override
	public String applyPlaceHolders(String message) {
		return super.applyPlaceHolders(message).replace("%amount%", String.valueOf(amount)).replace("%reliability%", String.valueOf(reliability)).replace("%cheat%", cheat.getName());
	}
}
