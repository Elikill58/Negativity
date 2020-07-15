package com.elikill58.negativity.universal;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.utils.Utils;

public class Messages {

	public static String getMessage(Object p, String dir, Object... placeholders) {
		return null;
	}

	public static String sendMessage(Object p, String dir, Object... placeholders) {
		return null;
	}

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

	public static void sendMessage(Player p, String dir, Object... placeholders) {
		String msg = getMessage(p, dir, placeholders);
		if (!msg.equalsIgnoreCase(dir))
			p.sendMessage(msg);
	}

	public static void sendMessageList(Player p, String dir, Object... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders)) {
			if (!s.equalsIgnoreCase(dir))
				p.sendMessage(Utils.coloredMessage(s));
		}
	}
}
