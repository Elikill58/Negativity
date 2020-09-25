package com.elikill58.negativity.universal;

import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.universal.account.NegativityAccount;

public class Messages {

	public static String getMessage(String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static String getMessage(UUID uuid, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(NegativityAccount.get(uuid).getLang(), dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static String getMessage(CommandSender sender, String dir, Object... placeholders) {
		String lang = (sender instanceof Player ? TranslatedMessages.getLang(((Player) sender).getUniqueId()) : TranslatedMessages.DEFAULT_LANG);
		String message = TranslatedMessages.getStringFromLang(lang, dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static String getMessage(NegativityAccount account, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(account.getLang(), dir, placeholders);
		return Utils.coloredMessage(message);
	}

	public static void sendMessage(CommandSender p, String dir, Object... placeholders) {
		p.sendMessage(getMessage(p, dir, placeholders));
	}

	public static void sendMessageList(CommandSender sender, String dir, Object... placeholders) {
		String lang = (sender instanceof Player ? TranslatedMessages.getLang(((Player) sender).getUniqueId()) : TranslatedMessages.DEFAULT_LANG);
		List<String> lines = TranslatedMessages.getStringListFromLang(lang, dir, placeholders);
		if(lines.isEmpty()) {
			lines.add(dir);
		}
		for (String s : lines) {
			sender.sendMessage(Utils.coloredMessage(s));
		}
	}
}
