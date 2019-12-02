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

	public static String getMessage(String dir, String... placeholders) {
		String message = "";
		try {
			message = TranslatedMessages.getStringFromLang(TranslatedMessages.getDefaultLang(), dir);
			if (message.equalsIgnoreCase(""))
				return dir;
		} catch (NullPointerException e) {
			System.out.println("Unknow ! default: " + Adapter.getAdapter().getStringInConfig("Translation.default") + " Get: " + TranslatedMessages.getDefaultLang());
		}
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		return Utils.coloredMessage("&r" + message);
	}

	public static Text getMessage(MessageReceiver receiver, String dir, String... placeholders) {
		return Text.of(getStringMessage(receiver, dir, placeholders));
	}

	public static Text getMessage(NegativityAccount account, String dir, String... placeholders) {
		return Text.of(getStringMessage(account.getLang(), dir, placeholders));
	}

	public static String getStringMessage(MessageReceiver receiver, String dir, String... placeholders) {
		return getStringMessage(getLang(receiver), dir, placeholders);
	}

	private static String getStringMessage(String lang, String dir, String... placeholders) {
		String message = TranslatedMessages.getStringFromLang(lang, dir);
		for (int index = 0; index <= placeholders.length - 1; index += 2)
			message = message.replaceAll(placeholders[index], placeholders[index + 1]);
		if (message.equalsIgnoreCase("&rnull"))
			return dir;
		return Utils.coloredMessage("&r" + message);
	}

	public static void sendMessage(MessageReceiver receiver, String dir, String... placeholders) {
		try {
			receiver.sendMessage(getMessage(receiver, dir, placeholders));
		} catch (Exception e) {
			receiver.sendMessage(Text.builder("[Negativity] " + dir + " not found. (Code error: " + e.getMessage() + ")").color(TextColors.RED).build());
		}
	}

	public static void sendMessageList(MessageReceiver receiver, String dir, String... placeholders) {
		for (String s : TranslatedMessages.getStringListFromLang(getLang(receiver), dir)) {
			for (int index = 0; index <= placeholders.length - 1; index += 2)
				s = s.replaceAll(placeholders[index], placeholders[index + 1]);
			receiver.sendMessage(TextSerializers.FORMATTING_CODE.deserialize(s));
		}
	}

	private static String getLang(MessageReceiver receiver) {
		return receiver instanceof Player ? TranslatedMessages.getLang(((Player) receiver).getUniqueId()) : TranslatedMessages.getDefaultLang();
	}

	public static void broadcastMessageList(String dir, String... placeholders) {
		for (Player p : Utils.getOnlinePlayers())
			sendMessageList(p, dir, placeholders);
	}
}
