package com.elikill58.negativity.universal.webhooks;

import java.util.List;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.webhooks.integrations.DiscordWebhook;
import com.elikill58.negativity.universal.webhooks.integrations.TelegramWebhook;
import com.elikill58.negativity.universal.webhooks.messages.WebhookMessage;

import java.util.ArrayList;
import java.util.Arrays;

public class WebhookManager {
	
	private static final List<Webhook> WEBHOOKS = new ArrayList<>();
	private static boolean enabled = false;
	
	public static void init() {
		WEBHOOKS.forEach(Webhook::runQueue); // clean queue
		WEBHOOKS.clear();
		Adapter ada = Adapter.getAdapter();
		Configuration config = ada.getConfig().getSection("webhooks");
		if(config == null)
			return;
		enabled = config.getBoolean("enable", false);
		if(!enabled)
			return;
		Configuration allWebHook = config.getSection("hook");
		allWebHook.getKeys().forEach((key) -> {
			Configuration hook = allWebHook.getSection(key);
			String type = hook.getString("type");
			if(type == null) {
				ada.getLogger().warn("You forget to register 'type' for the hook " + key + ".");
				return;
			}
			if(type.equalsIgnoreCase("discord")) {
				WEBHOOKS.add(new DiscordWebhook(hook));
			} else if(type.equalsIgnoreCase("telegram")) {
				WEBHOOKS.add(new TelegramWebhook(hook));
			} else {
				ada.getLogger().warn("Unknow webhook type " + type + ".");
			}
		});
		ada.getScheduler().runRepeating(() -> {
			WEBHOOKS.forEach(Webhook::runQueue);
		}, 20);
	}
	
	public static void send(WebhookMessage msg) {
		if(!enabled)
			return;
		WEBHOOKS.forEach((w) -> {
			try {
				w.send(msg.getMessageType(), msg.getConcerned(), Arrays.asList(msg));
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().error("Error while using webhook " + w.getWebhookName() + ": " + e.getMessage() + " (" + e.getStackTrace()[0].toString() + ")");
			}
		});
	}
	
	public static void addToQueue(WebhookMessage msg) {
		if(!enabled)
			return;
		WEBHOOKS.forEach((w) -> {
			try {
				w.addToQueue(msg);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().error("Error while using webhook " + w.getWebhookName() + ": " + e.getMessage() + " (" + e.getStackTrace()[0].toString() + ")");
			}
		});
	}
	
	public static boolean isEnabled() {
		return enabled;
	}
	
	public static List<Webhook> getWebhooks() {
		return WEBHOOKS;
	}
}
