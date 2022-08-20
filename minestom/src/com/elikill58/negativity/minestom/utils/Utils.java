package com.elikill58.negativity.minestom.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.minestom.MinestomNegativity;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class Utils {

	public static String coloredMessage(String msg) {
		return ChatColor.translateAlternateColorCodes('§', msg);
	}

	@Nullable
	public static ServerPlayerEntity getFirstOnlinePlayer() {
		Collection<ServerPlayerEntity> onlinePlayers = MinestomNegativity.getOnlinePlayers();
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

	public static float getPlayerHeadHeight(ServerPlayerEntity p) {
		float f = 1.62F;
		if (p.isSleeping()) {
			f = 0.2F;
		}
		if (p.isSneaking()) {
			f -= 0.08F;
		}
		return f;
	}

	public static float getEntityHeadHeight(net.minecraft.entity.Entity et) {
		return 1.74F;
	}

	public static Vec3d getPlayerVec(ServerPlayerEntity p) {
		Vec3d loc = p.getPos();
		return new Vec3d(loc.getX(), loc.getY() + getPlayerHeadHeight(p), loc.getZ());
	}
}
