package com.elikill58.negativity.spigot;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;

public class Messages {

	public static String getMessage(String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static String getMessage(Player p, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static String getMessage(NegativityAccount account, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(account.getLang(), dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static void sendMessage(CommandSender p, String dir, Object... placeholders) {
		if (p instanceof Player) {
			sendMessage((Player) p, dir, placeholders);
		} else {
			String msg = getMessage(dir, placeholders);
			if (!msg.equalsIgnoreCase(dir))
				p.sendMessage(msg);
		}
	}

	public static void sendMessage(Player p, String dir, Object... placeholders) {
		String msg = getMessage(p, dir, placeholders);
		if (!msg.equalsIgnoreCase(dir))
			p.sendMessage(msg);
	}

	public static void sendMessageList(CommandSender p, String dir, Object... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders)) {
			if (!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}

	public static void sendMessageList(Player p, String dir, Object... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders)) {
			if (!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}

	public static void broadcastMessageList(String dir, Object... placeholders) {
		for (Player p : Utils.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
