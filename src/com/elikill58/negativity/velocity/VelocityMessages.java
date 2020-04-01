package com.elikill58.negativity.velocity;

import com.elikill58.negativity.universal.TranslatedMessages;
import com.velocitypowered.api.proxy.Player;

import net.kyori.text.TextComponent;
import net.kyori.text.serializer.legacy.LegacyComponentSerializer;

public class VelocityMessages {

	public static TextComponent getMessage(Player p, String dir, Object... placeholders) {
		return coloredBungeeMessage(getStringMessage(p, dir, placeholders));
	}

	public static String getStringMessage(Player p, String dir, Object... placeholders) {
		return TranslatedMessages.getStringFromLang(TranslatedMessages.getLang(p.getUniqueId()), dir, placeholders);
	}

	public static TextComponent coloredBungeeMessage(String msg) {
		return LegacyComponentSerializer.legacy().deserialize(msg, '&');
	}
}
