package com.elikill58.negativity.bungee;

import com.elikill58.negativity.universal.TranslatedMessages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessages {

	public static String getMessage(ProxiedPlayer p, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders);
		return coloredBungeeMessage(message);
	}

	public static String coloredBungeeMessage(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
