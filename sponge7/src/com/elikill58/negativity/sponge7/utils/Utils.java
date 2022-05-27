package com.elikill58.negativity.sponge7.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.sponge7.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.flowpowered.math.vector.Vector3d;

public class Utils {

	public static String coloredMessage(String msg) {
		return TextSerializers.FORMATTING_CODE.replaceCodes(msg, '\u00a7');
	}

	@Nullable
	public static Player getFirstOnlinePlayer() {
		Collection<Player> onlinePlayers = Sponge.getServer().getOnlinePlayers();
		return onlinePlayers.isEmpty() ? null : onlinePlayers.iterator().next();
	}

	public static HashMap<String, String> getModsNameVersionFromMessage(String modName) {
		HashMap<String, String> mods = new HashMap<String, String>();
		String caractere = "abcdefghijklmnopqrstuvwxyz0123456789.,_-;";
		List<String> listCaracters = Arrays.asList(caractere.split(""));
		String s = "", name = "", version = "unknow";
		boolean isVersion = false, checkVersion = false;
		for (String parts : modName.split("")) {
			if (listCaracters.contains(parts.toLowerCase(Locale.ROOT))) {
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

	public static void removePotionEffect(PotionEffectData effects, PotionEffectType effectType) {
		for (PotionEffect effect : effects.getListValue()) {
			if (effect.getType().equals(effectType)) {
				effects.remove(effect);
			}
		}
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

	public static Vector3d getPlayerVec(Player p) {
		Location<World> loc = p.getLocation();
		return new Vector3d(loc.getX(), loc.getY() + getPlayerHeadHeight(p), loc.getZ());
	}
	
	public static ItemStack getMcLeaksIndicator(Player player, NegativityPlayer nPlayer) {
		ItemStack indicator = ItemStack.of(ItemTypes.WOOL);
		boolean usesMcLeaks = nPlayer.getAccount().isMcLeaks();
		indicator.offer(Keys.DYE_COLOR, usesMcLeaks ? DyeColors.RED : DyeColors.LIME);
		indicator.offer(Keys.DISPLAY_NAME, Messages.getMessage(player, "inventory.main.mcleaks_indicator." + (usesMcLeaks ? "positive" : "negative")));
		indicator.offer(Keys.ITEM_LORE, Collections.singletonList(Messages.getMessage(player, "inventory.main.mcleaks_indicator.description")));
		return indicator;
	}
}
