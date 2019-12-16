package com.elikill58.negativity.sponge;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;

public class Messages {

	public static String getMessage(String dir, Object... placeholders) {
		String message = "";
		try {
			message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir, placeholders);
			if (message.equalsIgnoreCase(""))
				return dir;
		} catch (NullPointerException e) {
			System.out.println("Unknow ! default: " + Adapter.getAdapter().getStringInConfig("Translation.default") + " Get: " + TranslatedMessages.getDefaultLang());
		}
		return Utils.coloredMessage("&r" + message);
	}

	public static Text getMessage(MessageReceiver receiver, String dir, Object... placeholders) {
		return Text.of(getStringMessage(receiver, dir, placeholders));
	}

	public static Text getMessage(NegativityAccount account, String dir, Object... placeholders) {
		return Text.of(getStringMessage(account.getLang(), dir, placeholders));
	}

	public static String getStringMessage(MessageReceiver receiver, String dir, Object... placeholders) {
		return getStringMessage(getLang(receiver), dir, placeholders);
	}

	private static String getStringMessage(String lang, String dir, Object... placeholders) {
		String message = TranslatedMessages.getStringFromLang(lang, dir, placeholders);
		return Utils.coloredMessage("&r" + message);
	}

	public static void sendMessage(MessageReceiver receiver, String dir, Object... placeholders) {
		try {
			receiver.sendMessage(getMessage(receiver, dir, placeholders));
		} catch (Exception e) {
			receiver.sendMessage(Text.builder("[Negativity] " + dir + " not found. (Code error: " + e.getMessage() + ")").color(TextColors.RED).build());
		}
	}

	public static void sendMessageList(MessageReceiver receiver, String dir, Object... placeholders) {
		for (String rawLine : TranslatedMessages.getStringListFromLang(getLang(receiver), dir, placeholders)) {
			receiver.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(rawLine));
		}
	}

	private static String getLang(MessageReceiver receiver) {
		return receiver instanceof Player ? TranslatedMessages.getLang(((Player) receiver).getUniqueId()) : TranslatedMessages.getDefaultLang();
	}

	public static void broadcastMessageList(String dir, Object... placeholders) {
		for (Player p : Utils.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
