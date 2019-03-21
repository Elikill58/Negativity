package com.elikill58.negativity.spigot.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;

@SuppressWarnings("deprecation")
public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];
	public static final ClickableText MESSAGE_UPDATE = new ClickableText().addOpenURLHoverEvent(ChatColor.YELLOW + "New version available (" + UniversalUtils.getLatestVersion().orElse("unknow") +  "). " + ChatColor.BOLD + "Download it here.", "Click here", "https://www.spigotmc.org/resources/48399/");

	public static int getMultipleOf(int i, int multiple, int more) {
		while (i % multiple != 0)
			i += more;
		return i;
	}

	public static String coloredMessage(String msg) {
		return msg.replaceAll("&0", String.valueOf(ChatColor.BLACK))
				.replaceAll("&1", String.valueOf(ChatColor.DARK_BLUE))
				.replaceAll("&2", String.valueOf(ChatColor.DARK_GREEN))
				.replaceAll("&3", String.valueOf(ChatColor.DARK_AQUA))
				.replaceAll("&4", String.valueOf(ChatColor.DARK_RED))
				.replaceAll("&5", String.valueOf(ChatColor.DARK_PURPLE))
				.replaceAll("&6", String.valueOf(ChatColor.GOLD)).replaceAll("&7", String.valueOf(ChatColor.GRAY))
				.replaceAll("&8", String.valueOf(ChatColor.DARK_GRAY)).replaceAll("&9", String.valueOf(ChatColor.BLUE))
				.replaceAll("&a", String.valueOf(ChatColor.GREEN)).replaceAll("&b", String.valueOf(ChatColor.AQUA))
				.replaceAll("&c", String.valueOf(ChatColor.RED))
				.replaceAll("&d", String.valueOf(ChatColor.LIGHT_PURPLE))
				.replaceAll("&e", String.valueOf(ChatColor.YELLOW)).replaceAll("&f", String.valueOf(ChatColor.WHITE))
				.replaceAll("&k", String.valueOf(ChatColor.MAGIC)).replaceAll("&l", String.valueOf(ChatColor.BOLD))
				.replaceAll("&m", String.valueOf(ChatColor.STRIKETHROUGH))
				.replaceAll("&n", String.valueOf(ChatColor.UNDERLINE))
				.replaceAll("&o", String.valueOf(ChatColor.ITALIC)).replaceAll("&r", String.valueOf(ChatColor.RESET));
	}

	public static ItemStack createItem(Material m, String name, String... lore) {
		return createItem(m, name, 1, lore);
	}

	public static ItemStack createItem(Material m, String name, int quantite, String... lore) {
		ItemStack item = new ItemStack(m, quantite);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack createItem(Material m, String name, int amount, byte b, String... lore) {
		ItemStack item = new ItemStack(m, amount, b);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(Arrays.asList(lore));
		item.setItemMeta(meta);
		return item;
	}

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

	public static ItemStack createSkull(String name, int amount, String owner, String... lore) {
		ItemStack skull = new ItemStack(getMaterialWith1_13_Compatibility("SKULL_ITEM", "LEGACY_SKULL_ITEM"), 1, (byte) 3);
		SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();
		skullmeta.setDisplayName(ChatColor.RESET + name);
		skullmeta.setOwner(owner);
		List<String> lorel = new ArrayList<>();
		for (String s : lore)
			lorel.add(s);
		skullmeta.setLore(lorel);
		skull.setItemMeta(skullmeta);
		return skull;
	}

	public static int getPing(Player p) {
		try {
			Object object = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer").cast(p);
			Object entityPlayer = object.getClass().getMethod("getHandle").invoke(object);
			return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static Object getWorldServer(Location loc) {
		try {
			Object object = Class.forName("org.bukkit.craftbukkit." + VERSION + ".CraftWorld").cast(loc.getWorld());
			return object.getClass().getMethod("getHandle").invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int parseInPorcent(int i) {
		if (i > 100)
			return 100;
		else if (i < 0)
			return 0;
		else
			return i;
	}

	public static int parseInPorcent(double i) {
		return parseInPorcent((int) i);
	}

	public static void sendPacket(Player p, String packetdir, Class<?> type, Object send) {
		try {
			sendPacket(p, Class.forName(packetdir.replaceAll("<version>", VERSION).replaceAll("%version%", VERSION)).getConstructor(type).newInstance(send));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void sendPacket(Player p, Object packet) {
		try {
			Object playerConnection = getPlayerConnection(p);
			playerConnection.getClass()
					.getMethod("sendPacket", Class.forName("net.minecraft.server." + VERSION + ".Packet"))
					.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object getPlayerConnection(Player p) {
		try {
			Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer").cast(p);
			Object entityPlayer = craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
			return entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Class<?> getEnumPlayerInfoAction() {
		try {
			Class<?> playerInfo = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutPlayerInfo");
			for(Class<?> clazz : playerInfo.getDeclaredClasses()) {
				if(clazz.getName().contains("EnumPlayerInfoAction")) {
					return clazz;
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Material getMaterialWith1_13_Compatibility(String before1_13, String after1_13) {
		Material m = null;
		try {
			m = (Material) Material.class.getField(before1_13).get(Material.class);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e1) {
			try {
				m = (Material) Material.class.getField(after1_13).get(Material.class);
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e2) {
				e2.printStackTrace();
			} catch (NoSuchFieldException e) {
				Adapter.getAdapter().error("Failed to find Material: " + before1_13 + " (1.12 & -) and " + after1_13 + " (1.13 & +)");
			}
		}
		return m;
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

	public static Optional<Cheat> getCheatFromName(String s) {
		for (Cheat c : Cheat.values())
			if (c.getName().equalsIgnoreCase(s))
				return Optional.of(c);
		return Optional.empty();
	}

	public static Optional<Cheat> getCheatFromItem(Material m) {
		for (Cheat c : Cheat.values())
			if (c.getMaterial().equals(m))
				return Optional.of(c);
		return Optional.empty();
	}

	public static void sendUpdateMessageIfNeed(Player p) {
		if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "showAlert"))
			return;
		if(!(UniversalUtils.hasInternet() && !UniversalUtils.isLatestVersion(Optional.of(SpigotNegativity.getInstance().getDescription().getVersion()))))
			return;
		MESSAGE_UPDATE.sendToPlayer(p);
	}

	public static double getLastTPS() {
		double[] tps = getTPS();
		return tps[tps.length - 1];
	}
	
	public static double[] getTPS() {
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object tps = server.getClass().getField("recentTps").get(server);
			return (double[]) tps;
		} catch (Exception e) {
			e.printStackTrace();
			return new double[] {};
		}
	}
}
