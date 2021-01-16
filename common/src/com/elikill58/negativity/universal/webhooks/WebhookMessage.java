package com.elikill58.negativity.universal.webhooks;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class WebhookMessage {

	private final WebhookMessageType messageType;
	private final String sender;
	private final Player concerned;
	private final Object[] placeholders;
	private final String date;
	
	public WebhookMessage(WebhookMessageType messageType, Player concerned, String sender, long date, Object... placeholders) {
		this.messageType = messageType;
		this.concerned = concerned;
		this.sender = sender;
		this.date = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()));
		this.placeholders = placeholders;
	}
	
	/**
	 * Get the message type which correspond to the source of it
	 * 
	 * @return the message type
	 */
	public WebhookMessageType getMessageType() {
		return messageType;
	}
	
	/**
	 * The sender of this message
	 * 
	 * @return the sender or null if sent by console
	 */
	public String getSender() {
		return sender;
	}
	
	/**
	 * The concerned player of the message
	 * 
	 * @return the concerned player
	 */
	public Player getConcerned() {
		return concerned;
	}
	
	/**
	 * Get all messages placeholders
	 * 
	 * @return placeholders
	 */
	public Object[] getPlaceholders() {
		return placeholders;
	}
	
	/**
	 * Apply all placeholders available for this message
	 * 
	 * @param message the raw memssage
	 * @return the given message with all replaced object
	 */
	public String applyPlaceHolders(String message) {
		return UniversalUtils.replacePlaceholders(message, getPlaceholders()).replaceAll("%date%", date)
				.replaceAll("%name%", concerned.getName()).replaceAll("%uuid%", concerned.getUniqueId().toString())
				.replaceAll("%ip%", concerned.getIP()).replaceAll("%sender%", sender);
	}
	
	public static enum WebhookMessageType {
		ALERT,
		BAN,
		KICK,
		REPORT;
	}
}
