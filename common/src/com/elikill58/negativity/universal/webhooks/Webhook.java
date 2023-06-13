package com.elikill58.negativity.universal.webhooks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage.WebhookMessageType;

public abstract class Webhook {

	protected static final ExecutorService executor = Executors.newSingleThreadExecutor((r) -> new Thread(r, "negativity-webhook"));

	protected final String name;
	protected final Configuration config;
	protected final List<WebhookMessage> queue = new ArrayList<>();
	protected boolean enabled = true;
	protected long time = 0, cooldown = 0;

	public Webhook(String name, Configuration config) {
		this.name = name;
		this.config = config;
		this.enabled = config.getBoolean("enabled", true);
		this.cooldown = config.getLong("cooldown", 0);
	}

	/**
	 * Close webhook
	 */
	public void close() {
		if (!executor.isShutdown())
			executor.shutdown();
	}

	/**
	 * Get the webhook name
	 * 
	 * @return webhook name
	 */
	public String getWebhookName() {
		return name;
	}

	/**
	 * Add message to queue of given webhook
	 * 
	 * @param msg the message to send
	 */
	public void addToQueue(WebhookMessage msg) {
		if (msg == null || !enabled || !msg.canBeSend(config.getSection("messages." + msg.getMessageType().name().toLowerCase(Locale.ROOT))))
			return;
		queue.add(msg); // maybe another that can be combined will come
	}

	/**
	 * Run queue each seconds.
	 */
	public void runQueue() {
		if (time > System.currentTimeMillis() || !enabled) // should skip
			return;

		List<WebhookMessage> messages = new ArrayList<>(queue); // copy list
		messages.sort(Comparator.naturalOrder());
		this.queue.clear();

		List<WebhookMessage> combinedMessages = new ArrayList<>();
		// firstly, combine all
		while (!messages.isEmpty()) {
			WebhookMessage msg = messages.remove(0);
			if (!messages.isEmpty()) { // removed is last
				List<WebhookMessage> toRemove = new ArrayList<>();
				for (int i = 0; i < messages.size(); i++) {
					if (messages.size() <= i)
						break;
					WebhookMessage other = messages.get(i);
					WebhookMessage third = msg.combine(other);
					if (third != null) {
						msg = third;
						toRemove.add(other);
					}
				}
				messages.removeAll(toRemove);
			}
			combinedMessages.add(msg);
		}
		combinedMessages.stream().collect(Collectors.groupingBy(WebhookMessage::getMessageType, Collectors.groupingBy(WebhookMessage::getConcerned, Collectors.toList()))).forEach((type, messagesPerPlayer) -> {
			messagesPerPlayer.forEach((p, web) -> send(type, p, web));
		});
		
	}

	/**
	 * Send the given message with adapted style to own webhook
	 * 
	 * @param msg the message to send
	 */
	public void send(WebhookMessageType type, Player p, List<WebhookMessage> msg) {
		if (!config.getBoolean("messages." + type.name().toLowerCase(Locale.ROOT) + ".enabled", true))
			return;
		try {
			executor.execute(() -> sendAsync(type, p, msg));
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().printError("Error while executing async webhook message about " + p.getName(), e);
		}
	}

	/**
	 * Send test message to webhook
	 * 
	 * @param asker Who ask for webhook ping
	 * @return true if the message is well sent
	 */
	public abstract boolean ping(String asker);

	/**
	 * Clean all data for the given player. Called when the player left.
	 * 
	 * @param p the player that left
	 */
	public void clean(Player p) {
		queue.clear();
	}

	protected abstract void sendAsync(WebhookMessageType type, Player p, List<WebhookMessage> msg);
}
