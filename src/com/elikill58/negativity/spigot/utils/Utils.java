package com.elikill58.negativity.spigot.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Version;
import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];

	public static List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object craftServer = server.getClass().getField("server").get(server);
			Object getted = craftServer.getClass().getMethod("getOnlinePlayers").invoke(craftServer);
			if (getted instanceof Player[])
				for (Player obj : (Player[]) getted)
					list.add(obj);
			else if (getted instanceof List)
				for (Object obj : (List<?>) getted)
					list.add((Player) obj);
			else
				System.out.println("Unknow getOnlinePlayers");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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
					return (Block) p.getClass().getMethod("getTargetBlock", Set.class, int.class).invoke(p, (Set<Material>) Sets.newHashSet(transparentItem), distance);
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
}
