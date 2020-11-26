package com.elikill58.negativity.sponge8.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.sponge8.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class Utils {

	//public static String coloredMessage(String msg) {
	//	return LegacyComponentSerializer.legacySection().deserialize(msg);
	//}

	@Nullable
	public static Player getFirstOnlinePlayer() {
		Collection<ServerPlayer> onlinePlayers = Sponge.getServer().getOnlinePlayers();
		return onlinePlayers.isEmpty() ? null : onlinePlayers.iterator().next();
	}

	public static HashMap<String, String> getModsNameVersionFromMessage(String modName) {
		HashMap<String, String> mods = new HashMap<String, String>();
		String caractere = "abcdefghijklmnopqrstuvwxyz0123456789.,_-;";
		List<String> listCaracters = Arrays.asList(caractere.split(""));
		String s = "", name = "", version = "unknow";
		boolean isVersion = false, checkVersion = false;
		for (String parts : modName.split("")) {
			if (listCaracters.contains(parts.toLowerCase())) {
				if (checkVersion) {
					if (UniversalUtils.isInteger(parts)) {
						checkVersion = false;
						isVersion = true;
					}
				}
				s += parts;
			} else {
				if (isVersion) {
					version = s;
					isVersion = false;
				} else {
					name = s;
					checkVersion = true;
				}
				if (!(s.isEmpty() || s.equalsIgnoreCase("")) && !version.equalsIgnoreCase("unknow")) {
					mods.put(name, version);
					version = "unknow";
					name = "";
				}
				s = "";
			}
		}
		return mods;
	}

	public static float getPlayerHeadHeight(Player p) {
		float f = 1.62F;
		if (p.get(Keys.IS_SLEEPING).orElse(false)) {
			f = 0.2F;
		}
		if (p.get(Keys.IS_SNEAKING).orElse(false)) {
			f -= 0.08F;
		}
		return f;
	}

	public static float getEntityHeadHeight(Entity et) {
		return 1.74F;
	}

	public static ItemStack getMcLeaksIndicator(Player player, NegativityPlayer nPlayer) {
		boolean usesMcLeaks = nPlayer.getAccount().isMcLeaks();
		ItemStack indicator = ItemStack.of(usesMcLeaks ? ItemTypes.RED_WOOL : ItemTypes.LIME_WOOL);
		indicator.offer(Keys.DISPLAY_NAME, Messages.getMessage(player, "inventory.main.mcleaks_indicator." + (usesMcLeaks ? "positive" : "negative")));
		indicator.offerSingle(Keys.LORE, Messages.getMessage(player, "inventory.main.mcleaks_indicator.description"));
		return indicator;
	}
}
