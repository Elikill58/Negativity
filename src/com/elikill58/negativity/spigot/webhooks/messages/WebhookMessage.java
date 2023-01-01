package com.elikill58.negativity.spigot.webhooks.messages;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class WebhookMessage implements Comparable<WebhookMessage> {

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

	public long getDate() {
		return date;
	}

	/**
	 * Apply all placeholders available for this message
	 * 
	 * @param message the raw message
	 * @return the given message with all replaced object
	 */
	public String applyPlaceHolders(String message) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(concerned);
		return UniversalUtils.replacePlaceholders(UniversalUtils.replacePlaceholders(message, getPlaceholders()), "%date%", UniversalUtils.formatTime(date), "%name%", concerned.getName(),
				"%uuid%", concerned.getUniqueId(), "%ip%", np.getIP(), "%sender%", sender, "%player_version%", np.getPlayerVersion().getName(), "%server_version%",
				Version.getVersion().getName(), "%tps%", String.format("%.3f", Utils.getLastTPS()), "%ping%", np.ping, "%world%",
				concerned.getWorld() != null ? concerned.getWorld().getName() : "-");
	}

	/**
	 * Combine actual message instance with given one
	 * 
	 * @param msg the message to add
	 * @return the combined object, or null if not combined
	 */
	public @Nullable WebhookMessage combine(WebhookMessage msg) {
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

	public boolean canBeSend(@Nullable ConfigurationSection config) {
		return config != null && config.getBoolean("enabled", true);
	}

	@Override
	public int compareTo(WebhookMessage o) {
		return (int) (o.date - date);
	}

	public static enum WebhookMessageType {
		ALERT, KICK, REPORT;
	}
}
