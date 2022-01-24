package com.elikill58.negativity.universal.webhooks.messages;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class WebhookMessage {

	protected final WebhookMessageType messageType;
	protected final String sender;
	protected final Player concerned;
	protected final Object[] placeholders;
	protected final long date;
	
	public WebhookMessage(WebhookMessageType messageType, Player concerned, String sender, long date, Object... placeholders) {
		this.messageType = messageType;
		this.concerned = concerned;
		this.sender = sender;
		this.date = date;
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
		Adapter ada = Adapter.getAdapter();
		String sDate = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneId.systemDefault()));
		return UniversalUtils.replacePlaceholders(UniversalUtils.replacePlaceholders(message, getPlaceholders()), "%date%", sDate, "%name%", concerned.getName(),
				"%uuid%", concerned.getUniqueId().toString(), "%ip%", concerned.getIP(), "%sender%", sender,
				"%server_name%", concerned.getServerName(), "%player_version%", concerned.getPlayerVersion().getName(),
				"%server_version%", ada.getServerVersion().getName(), "%tps%", String.format("%.3f", ada.getLastTPS()),
				"%ping%", concerned.getPing(), "%world%", concerned.getWorld() != null ? concerned.getWorld().getName() : "-");
	}
	
	/**
	 * Combine actual message instance with given one
	 * 
	 * @param msg the message to add
	 * @return the combined object, or null if not combined
	 */
	public WebhookMessage combine(WebhookMessage msg) {
		return null;
	}
	
	/**
	 * Know if this type of webhook message can be combined.
	 * 
	 * @return true if can combine
	 */
	public boolean canCombine() {
		return false;
	}
	
	public static enum WebhookMessageType {
		ALERT,
		BAN,
		KICK,
		REPORT;
	}
}
