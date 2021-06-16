package com.elikill58.negativity.spigot.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.item.SpigotMaterial;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];

	public static List<Player> getOnlinePlayers() {
		return SpigotVersionAdapter.getVersionAdapter().getOnlinePlayers();
	}

	@Nullable
	public static Player getFirstOnlinePlayer() {
		List<Player> onlinePlayers = getOnlinePlayers();
		return onlinePlayers.isEmpty() ? null : onlinePlayers.iterator().next();
	}

	public static Effect getEffect(String effect) {
		Effect m = null;
		try {
			m = (Effect) Effect.class.getField(effect).get(Effect.class);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e1) {
			m = null;
		}
		return m;
	}
	
	public static Block getTargetBlock(Player p, int distance) {
		Material[] transparentItem = new Material[] {};
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_14)) {
				return (Block) p.getClass().getMethod("getTargetBlockExact", int.class).invoke(p, distance);
			} else {
				try {
					return (Block) p.getClass().getMethod("getTargetBlock", Set.class, int.class).invoke(p, Sets.newHashSet(transparentItem), distance);
				} catch (NoSuchMethodException e) {}
				try {
					HashSet<Byte> hashSet = new HashSet<>();
					for(Material m : transparentItem)
						hashSet.add((byte) m.getId());
					return (Block) p.getClass().getMethod("getTargetBlock", HashSet.class, int.class).invoke(p, hashSet, distance);
				} catch (NoSuchMethodException e) {}
			}
		} catch (Exception exc) {
			exc.printStackTrace();
		}
		return null;
	}

	
	public static ItemStack getItemFromString(String s) {
		Preconditions.checkNotNull(s, "Error while creating item. The material is null.");
		try {
			String[] splitted = s.toUpperCase(Locale.ROOT).split(":");
			String key = splitted[0];
			Material temp = null;
			try {
				temp = Material.valueOf(key);
			} catch (IllegalArgumentException e) {}
			if(temp == null && UniversalUtils.isInteger(key)) {
				try {
					temp = (Material) Material.class.getDeclaredMethod("getMaterial", int.class).invoke(null, Integer.parseInt(key));
				} catch (Exception e) {
					// method not found because of too recent version
				}
			}
			byte b = splitted.length > 1 ? Byte.parseByte(s.split(":")[1]) : -1;
			if(temp == null) {
				com.elikill58.negativity.api.item.Material ownMaterial = ItemRegistrar.getInstance().get(s);
				if(ownMaterial != null) {
					temp = (Material) ownMaterial.getDefault();
					b = ((SpigotMaterial) ownMaterial).getDamage();
				}
			}
			
			if(temp == null) {
				SpigotNegativity.getInstance().getLogger().warning("Error while creating item. Cannot find item for " + s + ".");
				return null;
			}
			return b != -1 ? new ItemStack(temp, 1, b) : new ItemStack(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
