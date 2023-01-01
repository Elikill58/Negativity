package com.elikill58.negativity.spigot.webhooks;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.webhooks.integrations.DiscordWebhook;
import com.elikill58.negativity.spigot.webhooks.messages.WebhookMessage;
import com.elikill58.negativity.universal.adapter.Adapter;

public class WebhookManager {
	
	private static final List<Webhook> WEBHOOKS = new ArrayList<>();
	private static boolean enabled = false;
	
	public static void init() {
		WEBHOOKS.forEach(Webhook::runQueue); // clean queue
		WEBHOOKS.clear();
		ConfigurationSection config = SpigotNegativity.getInstance().getConfig().getConfigurationSection("webhooks");
		if(config == null)
			return;
		enabled = config.getBoolean("enable", false);
		if(!enabled)
			return;
		Adapter ada = Adapter.getAdapter();
		ConfigurationSection allWebHook = config.getConfigurationSection("hook");
		allWebHook.getKeys(false).forEach((key) -> {
			ConfigurationSection hook = allWebHook.getConfigurationSection(key);
			String type = hook.getString("type");
			if(type == null) {
				ada.getLogger().warn("You forget to register 'type' for the hook " + key + ".");
				return;
			}
			if(type.equalsIgnoreCase("discord")) {
				WEBHOOKS.add(new DiscordWebhook(hook));
			} else {
				ada.getLogger().warn("Unknow webhook type " + type + ".");
			}
		});
		Bukkit.getScheduler().runTaskTimerAsynchronously(SpigotNegativity.getInstance(), () -> WEBHOOKS.forEach(Webhook::runQueue), 20, 20);
	}
	
	public static void send(WebhookMessage msg) {
		if(!enabled)
			return;
		WEBHOOKS.forEach((w) -> {
			try {
				w.send(msg);
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
