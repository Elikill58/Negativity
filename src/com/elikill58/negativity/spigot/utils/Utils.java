package com.elikill58.negativity.spigot.utils;

import java.lang.reflect.Method;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];

	public static int getMultipleOf(int i, int multiple, int more) {
		while (i % multiple != 0)
			i += more;
		return i;
	}

	public static String coloredMessage(String msg) {
		return ChatColor.translateAlternateColorCodes('&', msg);
	}

	public static List<String> coloredMessage(String... messages) {
		List<String> ret = new ArrayList<>();
		for (String message : messages) {
			ret.add(coloredMessage(message));
		}

		return ret;
	}

	public static ItemStack createItem(Material m, String name, String... lore) {
		return createItem(m, name, 1, lore);
	}

	public static ItemStack createItem(Material m, String name, int quantite, String... lore) {
		ItemStack item = new ItemStack(m, quantite);
		ItemMeta meta = (ItemMeta) item.getItemMeta();
		meta.setDisplayName(ChatColor.RESET + name);
		meta.setLore(coloredMessage(lore));
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
		ItemStack skull = new ItemStack(getMaterialWith1_15_Compatibility("PLAYER_HEAD", "SKULL_ITEM", "LEGACY_SKULL_ITEM"), 1, (byte) 3);
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
			if (playerConnection != null) {
				playerConnection.getClass()
						.getMethod("sendPacket", Class.forName("net.minecraft.server." + VERSION + ".Packet"))
						.invoke(playerConnection, packet);
			}

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
			for(Class<?> clazz : playerInfo.getDeclaredClasses())
				if(clazz.getName().contains("EnumPlayerInfoAction"))
					return clazz;
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

	public static Material getMaterialWith1_15_Compatibility(String... tempMat) {
		for(String s : tempMat) {
			try {
				Material m = (Material) Material.class.getField(s).get(Material.class);
				if(m != null)
					return m;
			} catch (IllegalArgumentException | IllegalAccessException | SecurityException e2) {
				e2.printStackTrace();
			} catch (NoSuchFieldException e) {}
		}
		String temp = "";
		for(String s : tempMat)
			temp = temp + (temp.equalsIgnoreCase("") ? "" : ", ") + s;
		Adapter.getAdapter().error("Failed to find Material " + temp);
		return null;
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
		if(UniversalUtils.isLatestVersion(SpigotNegativity.getInstance().getDescription().getVersion()))
			return;
		String newerVersion = UniversalUtils.getLatestVersion().orElse("unknow");
		new ClickableText().addOpenURLHoverEvent(
				ChatColor.YELLOW + "New version of Negativity available (" + newerVersion +  "). " + ChatColor.BOLD + "Download it here.",
				"Click here", "https://www.spigotmc.org/resources/48399/")
				.sendToPlayer(p);
	}

	public static double getLastTPS() {
		double[] tps = getTPS();
		return tps[tps.length - 1];
	}

	public static double[] getTPS() {
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			return (double[]) server.getClass().getField("recentTps").get(server);
		} catch (Exception e) {
			SpigotNegativity.getInstance().getLogger().warning("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
			return new double[] {20, 20, 20};
		}
	}
	
	public static String getInventoryTitle(InventoryView inv) {
		try {
			Object nextInv = inv;
			if(!Version.getVersion().isNewerOrEquals(Version.V1_14)) {
				nextInv = inv.getTopInventory();
			}
			Method getTitle = nextInv.getClass().getMethod("getTitle");
			getTitle.setAccessible(true);
			return (String) getTitle.invoke(nextInv);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getInventoryName(InventoryClickEvent e) {
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_14)) {
				Method m = e.getView().getClass().getMethod("getTitle");
				m.setAccessible(true);
				return (String) m.invoke(e.getView());
			} else {
				Method m = e.getClickedInventory().getClass().getMethod("getName");
				m.setAccessible(true);
				return (String) m.invoke(e.getClickedInventory());
			}
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}
}
