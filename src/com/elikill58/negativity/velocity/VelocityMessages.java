package com.elikill58.negativity.velocity;

import com.elikill58.negativity.universal.TranslatedMessages;
import com.velocitypowered.api.proxy.Player;

import net.kyori.text.TextComponent;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityMessages {

	public static TextComponent getMessage(String dir, String... placeholders) {
		String message = "";
		try {
			message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(TranslatedMessages.getDefaultLang() + " unknow. default: " + TranslatedMessages.DEFAULT_LANG + " Get: " + TranslatedMessages.getDefaultLang());
		}
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("§rnull"))
			return coloredBungeeMessage(dir);
		return coloredBungeeMessage(message);
	}

	public static TextComponent getMessage(Player p, String dir, String... placeholders) {
		return coloredBungeeMessage(getStringMessage(p, dir, placeholders));
	}

	public static String getStringMessage(Player p, String dir, String... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir);
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return message;
	}

	public static TextComponent coloredBungeeMessage(String msg) {
		return LegacyComponentSerializer.legacy().deserialize(msg, '&');
				/*msg.replaceAll("&0", String.valueOf(TextColor.BLACK))
				.replaceAll("&1", String.valueOf(TextColor.DARK_BLUE))
				.replaceAll("&2", String.valueOf(TextColor.DARK_GREEN))
				.replaceAll("&3", String.valueOf(TextColor.DARK_AQUA))
				.replaceAll("&4", String.valueOf(TextColor.DARK_RED))
				.replaceAll("&5", String.valueOf(TextColor.DARK_PURPLE))
				.replaceAll("&6", String.valueOf(TextColor.GOLD))
				.replaceAll("&7", String.valueOf(TextColor.GRAY))
				.replaceAll("&8", String.valueOf(TextColor.DARK_GRAY))
				.replaceAll("&9", String.valueOf(TextColor.BLUE))
				.replaceAll("&a", String.valueOf(TextColor.GREEN))
				.replaceAll("&b", String.valueOf(TextColor.AQUA))
				.replaceAll("&c", String.valueOf(TextColor.RED))
				.replaceAll("&d", String.valueOf(TextColor.LIGHT_PURPLE))
				.replaceAll("&e", String.valueOf(TextColor.YELLOW))
				.replaceAll("&f", String.valueOf(TextColor.WHITE));
				.replaceAll("&k", String.valueOf(TextColor.MAGIC))
				.replaceAll("&l", String.valueOf(TextColor.BOLD))
				.replaceAll("&m", String.valueOf(TextColor.STRIKETHROUGH))
				.replaceAll("&n", String.valueOf(TextColor.UNDERLINE))
				.replaceAll("&o", String.valueOf(TextColor.ITALIC))
				.replaceAll("&r", String.valueOf(TextColor.RESET))*/
	}
}
