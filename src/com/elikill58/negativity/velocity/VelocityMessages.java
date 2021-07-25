package com.elikill58.negativity.velocity;

import com.elikill58.negativity.universal.TranslatedMessages;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class VelocityMessages {

	public static TextComponent getMessage(Player p, String dir, Object... placeholders) {
		return coloredBungeeMessage(getStringMessage(p, dir, placeholders));
	}

	public static String getStringMessage(Player p, String dir, Object... placeholders) {
		return TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders);
	}

	public static void sendMessage(CommandSource source, String message, String... placeholders) {
		if (source instanceof Player) {
			source.sendMessage(getMessage((Player) source, message, (Object[]) placeholders));
		} else {
			source.sendMessage(coloredBungeeMessage(TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), message, (Object[]) placeholders)));
		}
	}

	public static TextComponent coloredBungeeMessage(String msg) {
		return Component.text(msg.replaceAll("&", "§"));
	}
}
