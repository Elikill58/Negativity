package com.elikill58.negativity.bungee;

import com.elikill58.negativity.universal.TranslatedMessages;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessages {

	public static String getMessage(String dir, Object... placeholders) {
		String message = "";
		try {
			message = ChatColor.RESET
					+ TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(TranslatedMessages.getDefaultLang() + " unknow. default: " + TranslatedMessages.DEFAULT_LANG + " Get: " + TranslatedMessages.getDefaultLang());
		}
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return coloredBungeeMessage(message);
	}

	public static String getMessage(ProxiedPlayer p, String dir, Object... placeholders) {
		String message = ChatColor.RESET + TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return coloredBungeeMessage(message);
	}

	public static String coloredBungeeMessage(String msg) {
		return msg.replaceAll("&0", String.valueOf(ChatColor.BLACK))
				.replaceAll("&1", String.valueOf(ChatColor.DARK_BLUE))
				.replaceAll("&2", String.valueOf(ChatColor.DARK_GREEN))
				.replaceAll("&3", String.valueOf(ChatColor.DARK_AQUA))
				.replaceAll("&4", String.valueOf(ChatColor.DARK_RED))
				.replaceAll("&5", String.valueOf(ChatColor.DARK_PURPLE))
				.replaceAll("&6", String.valueOf(ChatColor.GOLD)).replaceAll("&7", String.valueOf(ChatColor.GRAY))
				.replaceAll("&8", String.valueOf(ChatColor.DARK_GRAY)).replaceAll("&9", String.valueOf(ChatColor.BLUE))
				.replaceAll("&a", String.valueOf(ChatColor.GREEN)).replaceAll("&b", String.valueOf(ChatColor.AQUA))
				.replaceAll("&c", String.valueOf(ChatColor.RED))
				.replaceAll("&d", String.valueOf(ChatColor.LIGHT_PURPLE))
				.replaceAll("&e", String.valueOf(ChatColor.YELLOW)).replaceAll("&f", String.valueOf(ChatColor.WHITE))
				.replaceAll("&k", String.valueOf(ChatColor.MAGIC)).replaceAll("&l", String.valueOf(ChatColor.BOLD))
				.replaceAll("&m", String.valueOf(ChatColor.STRIKETHROUGH))
				.replaceAll("&n", String.valueOf(ChatColor.UNDERLINE))
				.replaceAll("&o", String.valueOf(ChatColor.ITALIC)).replaceAll("&r", String.valueOf(ChatColor.RESET));
	}
}
