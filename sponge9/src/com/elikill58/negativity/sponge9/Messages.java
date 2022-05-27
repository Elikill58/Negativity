package com.elikill58.negativity.sponge9;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.account.NegativityAccount;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Messages {

	//public static String getMessage(String dir, Object... placeholders) {
	//	String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
	//	return Utils.coloredMessage(message);
	//}

	public static Component getMessage(Audience receiver, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(getLang(receiver), dir, placeholders);
		return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
	}

	public static Component getMessage(NegativityAccount account, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(account.getLang(), dir, placeholders);
		return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
	}

	//public static String getStringMessage(Audience receiver, String dir, Object... placeholders) {
	//	return getStringMessage(getLang(receiver), dir, placeholders);
	//}
	//
	//private static String getStringMessage(String lang, String dir, Object... placeholders) {
	//	String message = TranslatedMessages.getStringFromLang(lang, dir, placeholders);
	//	return Utils.coloredMessage(message);
	//}

	public static void sendMessage(Audience receiver, String dir, Object... placeholders) {
		receiver.sendMessage(getMessage(receiver, dir, placeholders));
	}

	public static void sendMessageList(Audience receiver, String dir, Object... placeholders) {
		for (String rawLine : TranslatedMessages.getStringListFromLang(getLang(receiver), dir, placeholders)) {
			receiver.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(rawLine));
		}
	}

	private static String getLang(Audience receiver) {
		return receiver instanceof Player ? TranslatedMessages.getLang(((Player) receiver).uniqueId()) : TranslatedMessages.getDefaultLang();
	}

	public static void broadcastMessageList(String dir, Object... placeholders) {
		for (Player p : Sponge.server().onlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
