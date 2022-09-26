package com.elikill58.negativity.fabric;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.account.NegativityAccount;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class Messages {

	public static String getMessage(String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
		return ChatColor.color(message);
	}

	public static Text getMessage(ServerPlayerEntity receiver, String dir, Object... placeholders) {
		return Text.of(getStringMessage(receiver, dir, placeholders));
	}

	public static Text getMessage(NegativityAccount account, String dir, Object... placeholders) {
		return Text.of(getStringMessage(account.getLang(), dir, placeholders));
	}

	public static String getStringMessage(ServerPlayerEntity receiver, String dir, Object... placeholders) {
		return getStringMessage(getLang(receiver), dir, placeholders);
	}

	private static String getStringMessage(String lang, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(lang, dir, placeholders);
		return ChatColor.color(message);
	}

	public static void sendMessage(ServerPlayerEntity receiver, String dir, Object... placeholders) {
		receiver.sendMessage(getMessage(receiver, dir, placeholders), false);
	}

	public static void sendMessageList(ServerPlayerEntity receiver, String dir, Object... placeholders) {
		for (String rawLine : TranslatedMessages.getStringListFromLang(getLang(receiver), dir, placeholders)) {
			receiver.sendMessage(Text.of(rawLine), false);
		}
	}

	private static String getLang(ServerPlayerEntity receiver) {
		return TranslatedMessages.getLang(((ServerPlayerEntity) receiver).getUuid());
	}

	public static void broadcastMessageList(String dir, Object... placeholders) {
		for (ServerPlayerEntity p : GlobalFabricNegativity.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
