package com.elikill58.negativity.spigot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.TranslatedMessages;

public class Messages {

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
		return Utils.coloredMessage(message);
	}

	public static String getMessage(Player p, String dir, Object... placeholders) {
		String message = ChatColor.RESET + TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return Utils.coloredMessage(message.replaceAll("%tps%", String.valueOf(Utils.getLastTPS())));
	}

	public static void sendMessage(CommandSender p, String dir, Object... placeholders) {
		try {
			if(p instanceof Player) {
				sendMessage((Player) p, dir, placeholders);
			} else {
				String msg = getMessage(dir, placeholders);
				if(!msg.equalsIgnoreCase(dir))
					p.sendMessage(msg);
			}
		} catch (Exception e) {
			p.sendMessage(ChatColor.RED + dir + " not found. (Code error: " + e.getCause() + ")");
		}
	}

	public static void sendMessage(Player p, String dir, Object... placeholders) {
		try {
			String msg = getMessage(p, dir, placeholders);
			if(!msg.equalsIgnoreCase(dir))
				p.sendMessage(msg);
		} catch (Exception e) {
			p.sendMessage(ChatColor.RED + dir + " not found. (Code error: " + e.getCause() + ")");
		}
	}

	public static void sendMessageList(CommandSender p, String dir, Object... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders)) {
			if(!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}

	public static void sendMessageList(Player p, String dir, Object... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders)) {
			if(!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}

	public static void broadcastMessageList(String dir, Object... placeholders) {
		for (Player p : Utils.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
