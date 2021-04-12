package com.elikill58.negativity.bungee;

import java.util.UUID;

import com.elikill58.negativity.universal.TranslatedMessages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessages {

	public static String getMessage(ProxiedPlayer p, String dir, Object... placeholders) {
		return getMessage(p.getUniqueId(), dir, placeholders);
	}

	public static String getMessage(UUID playerId, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(playerId), dir, placeholders);
		return coloredBungeeMessage(message);
	}

	public static void sendMessage(CommandSender receiver, String dir, Object... placeholders) {
		if (receiver instanceof ProxiedPlayer) {
			receiver.sendMessage(TextComponent.fromLegacyText(getMessage((ProxiedPlayer) receiver, dir, placeholders)));
		} else {
			receiver.sendMessage(TextComponent.fromLegacyText(TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders)));
		}
	}

	public static String coloredBungeeMessage(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
}
