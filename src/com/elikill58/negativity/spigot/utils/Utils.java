package com.elikill58.negativity.spigot.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.ReflectionUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class Utils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];

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

	public static List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		try {
			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer", "server.");
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

	public static ItemStack createSkull(String name, int amount, String owner, String... lore) {
		ItemStack skull = new ItemStack(ItemUtils.PLAYER_HEAD, 1, (byte) 3);
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
	
	/**
	 * Get the current player ping
	 * 
	 * @param p the player
	 * @return the player ping
	 */
	public static int getPing(Player p) {
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_16))
				return p.getPing();
			Object entityPlayer = PacketUtils.getEntityPlayer(p);
			return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
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

	public static void sendUpdateMessageIfNeed(Player p) {
		if(!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), Perm.SHOW_ALERT))
			return;
		if(UniversalUtils.isLatestVersion(SpigotNegativity.getInstance().getDescription().getVersion()))
			return;
		String newerVersion = UniversalUtils.getLatestVersion().orElse("unknow");
		new ClickableText().addOpenURLHoverEvent(
				ChatColor.YELLOW + "New version of Negativity available (" + newerVersion +  "). " + ChatColor.BOLD + "Download it here.",
				"Click here", "https://www.spigotmc.org/resources/48399/")
				.sendToPlayer(p);
	}
	
	public static long sumTps(long[] array) {
		long l = 0L;
		for (long m : array)
			l += m;
		return l / array.length;
	}

	public static double getLastTPS() {
		double[] tps = getTPS();
		return tps[0];
	}

	public static double[] getTPS() {
		if(SpigotNegativity.isCraftBukkit) {
			return new double[] {20, 20, 20};
		} else {
			try {
				Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer", "server.");
				Object server = mcServer.getMethod("getServer").invoke(mcServer);
				return (double[]) server.getClass().getField("recentTps").get(server);
			} catch (Exception e) {
				SpigotNegativity.getInstance().getLogger().warning("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
				e.printStackTrace();
				return new double[] {20, 20, 20};
			}
		}
	}
	
	public static void teleportPlayerOnGround(Player p) {
		int i = 20;
		Location loc = p.getLocation();
		while (loc.getBlock().getType().equals(Material.AIR) && i > 0) {
			loc.subtract(0, 1, 0);
			i--;
		}
		p.teleport(loc.add(0, 1, 0));
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
	
	public static boolean isInBoat(Player p) {
		return p.isInsideVehicle() && p.getVehicle().getType().equals(EntityType.BOAT);
	}

	public static boolean hasThorns(Player p) {
		ItemStack[] armor = p.getInventory().getArmorContents();
		if(armor == null)
			return false;
		for(ItemStack item : armor)
			if(item != null && (item.containsEnchantment(Enchantment.THORNS) || item.getType().name().startsWith("NETHERITE")))
				return true;
		return false;
	}
	
	public static ItemStack getItemInHand(Player p) {
		return p.getItemInHand();
	}
	
	public static ItemStack getItemInOffHand(Player p) {
		try {
			return (ItemStack) ReflectionUtils.callMethod(p.getInventory(), "getItemInOffHand");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Entity getEntityByID(int i) {
		for(World w : Bukkit.getWorlds()) {
			Optional<Entity> opt = w.getEntities().stream().filter((et) -> et.getEntityId() == i).findFirst();
			if(opt.isPresent())
				return opt.get();
 		}
		return null;
	}
	
	/**
	 * Check if a player is swimming. Compatible even before 1.13
	 * 
	 * @param p the player to check if he is swimming
	 * @return true if the player is swimming
	 */
	public static boolean isSwimming(Player p) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_13) && (p.isSwimming() || p.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)))
			return true;
		else {
			Location loc = p.getLocation().clone();
			if(loc.getBlock().getType().name().contains("WATER"))
				return true;
			return loc.subtract(0, 1, 0).getBlock().getType().name().contains("WATER");
		}
	}

	/**
	 * Get the X/Z speed.
	 * 
	 * @param from Location where the player comes from
	 * @param to Location where the player go
	 * @return the speed (without count Y)
	 */
	public static double getSpeed(Location from, Location to) {
		double x = to.getX() - from.getX();
		double z = to.getZ() - from.getZ();

		return x * x + z * z;
	}

	public static double getSpeed(Location from, Location to, Vector dir) {
		double x = to.getX() - from.getX() - dir.getX();
		double z = to.getZ() - from.getZ() - dir.getZ();

		return x * x + z * z;
	}
	
	public static PotionEffect getPotionEffect(Player p, PotionEffectType type) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_12))
			return p.getPotionEffect(type);
		for(PotionEffect pe : p.getActivePotionEffects())
			if(pe.getType().equals(type))
				return pe;
		return null;
	}
	
	public static void hidePlayer(Player p, Player cible) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_13))
			p.hidePlayer(SpigotNegativity.getInstance(), cible);
		else
			p.hidePlayer(cible);
	}
	
	public static void showPlayer(Player p, Player cible) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_13))
			p.showPlayer(SpigotNegativity.getInstance(), cible);
		else
			p.showPlayer(cible);
	}
}
