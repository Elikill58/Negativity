package com.elikill58.negativity.spigot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;

public class Messages {

	public static String getMessage(String dir, String... placeholders) {
		String message = "";
		try {
			message = ChatColor.RESET
					+ TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir);
		} catch (NullPointerException e) {
			e.printStackTrace();
			System.out.println(TranslatedMessages.getDefaultLang() + " unknow. default: " + TranslatedMessages.DEFAULT_LANG + " Get: " + TranslatedMessages.getDefaultLang());
		}
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return Utils.coloredMessage(message);
	}
	
	public static String getMessage(Player p, String dir, String... placeholders) {
		String message = ChatColor.RESET + TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(Adapter.getAdapter().getNegativityAccount(p.getUniqueId())), dir);
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("§rnull"))
			return dir;
		return Utils.coloredMessage(message.replaceAll("%tps%", String.valueOf(Utils.getLastTPS())));
	}

	public static void sendMessage(CommandSender p, String dir, String... placeholders) {
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

	public static void sendMessage(Player p, String dir, String... placeholders) {
		try {
			String msg = getMessage(p, dir, placeholders);
			if(!msg.equalsIgnoreCase(dir))
				p.sendMessage(msg);
		} catch (Exception e) {
			p.sendMessage(ChatColor.RED + dir + " not found. (Code error: " + e.getCause() + ")");
		}
	}

	public static void sendMessageList(CommandSender p, String dir, String... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getDefaultLang(), dir)) {
			for (int index = 0; index <= placeholders.length - 1; index += 2)
				s = s.replaceAll(placeholders[index], placeholders[index + 1]);
			if(!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}

	public static void sendMessageList(Player p, String dir, String... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getLang(Adapter.getAdapter().getNegativityAccount(p.getUniqueId())), dir)) {
			for (int index = 0; index <= placeholders.length - 1; index += 2)
				s = s.replaceAll(placeholders[index], placeholders[index + 1]);
			if(!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}

	public static void broadcastMessageList(String dir, String... placeholders) {
		for (Player p : Utils.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
