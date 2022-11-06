package com.elikill58.negativity.minestom;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.account.NegativityAccount;

import net.minestom.server.entity.Player;

public class Messages {

	public static String getMessage(String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
		return coloredMessage(message);
	}

	public static String getMessage(Player receiver, String dir, Object... placeholders) {
		return getStringMessage(receiver, dir, placeholders);
	}

	public static String getMessage(NegativityAccount account, String dir, Object... placeholders) {
		return getStringMessage(account.getLang(), dir, placeholders);
	}

	public static String getStringMessage(Player receiver, String dir, Object... placeholders) {
		return getStringMessage(getLang(receiver), dir, placeholders);
	}

	private static String getStringMessage(String lang, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(lang, dir, placeholders);
		return coloredMessage(message);
	}

	public static String coloredMessage(String msg) {
		return ChatColor.translateAlternateColorCodes('§', msg);
	}

	public static void sendMessage(Player receiver, String dir, Object... placeholders) {
		receiver.sendMessage(getMessage(receiver, dir, placeholders));
	}

	public static void sendMessageList(Player receiver, String dir, Object... placeholders) {
		for (String rawLine : TranslatedMessages.getStringListFromLang(getLang(receiver), dir, placeholders)) {
			receiver.sendMessage(rawLine);
		}
	}

	private static String getLang(Player receiver) {
		return TranslatedMessages.getLang(((Player) receiver).getUuid());
	}

	public static void broadcastMessageList(String dir, Object... placeholders) {
		for (Player p : MinestomNegativity.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
