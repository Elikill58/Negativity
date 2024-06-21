package com.elikill58.negativity.spigot.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.utils.ReflectionUtils;

public class PacketUtils {

	/**
	 * This Map is to reduce Reflection action which take more resources than just
	 * RAM action
	 */
	private static final HashMap<String, Class<?>> ALL_CLASS = new HashMap<>();

	private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];
	private static final boolean isNewSystem = isNewSystem();
	public static final String NMS_PREFIX = isNewSystem ? "net.minecraft." : "net.minecraft.server." + VERSION + ".";
	
	private static boolean isNewSystem() {
		try {
			Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			return false;
		} catch (Exception e) {
		}
		return true;
	}

	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name of the NMS class (in net.minecraft.server package ONLY, because
	 *             it's NMS)
	 * @return the loaded or cached class
	 */
	public static Class<?> getNmsClass(String name) {
		synchronized (ALL_CLASS) {
			return ALL_CLASS.computeIfAbsent(name, (s) -> {
				try {
					return Class.forName(NMS_PREFIX + name);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			});
		}
	}

	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name          of the NMS class (in net.minecraft.server package ONLY,
	 *                      because it's NMS)
	 * @param packagePrefix the prefix of the package for 1.17+
	 * @return the loaded or cached class
	 */
	public static Class<?> getNmsClass(String name, String packagePrefix) {
		synchronized (ALL_CLASS) {
			return ALL_CLASS.computeIfAbsent(name, (s) -> {
				try {
					return Class.forName(NMS_PREFIX + (isNewSystem ? packagePrefix : "") + name);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			});
		}
	}

	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name of the OBC class (in org.bukkit.craftbukkit.version. package
	 *             ONLY, because it's OBC)
	 * @return the loaded or cached class
	 */
	public static Class<?> getObcClass(String name) {
		synchronized (ALL_CLASS) {
			return ALL_CLASS.computeIfAbsent(name, (s) -> {
				try {
					return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			});
		}
	}

	/**
	 * Get NMS entity player of specified one
	 * 
	 * @param p the player that we want the NMS entity player
	 * @return the entity player
	 */
	public static Object getEntityPlayer(Player p) {
		try {
			return getObcClass("entity.CraftPlayer").getDeclaredMethod("getHandle").invoke(p);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Object getNMSEntity(Entity e) {
		try {
			return getObcClass("entity.CraftEntity").getDeclaredMethod("getHandle").invoke(e);
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	/**
	 * Get NMS server
	 * 
	 * @return the actual NMS server
	 */
	public static Object getDedicatedServer() {
		try {
			return getObcClass("CraftServer").getDeclaredMethod("getServer").invoke(Bukkit.getServer());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the NMS world server
	 * 
	 * @param loc the location in the world of which we want to get the NMS world
	 *            server
	 * @return the world server of location's world
	 */
	public static Object getWorldServer(Location loc) {
		try {
			return getObcClass("CraftWorld").getMethod("getHandle").invoke(loc.getWorld());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the NMS world server
	 * 
	 * @param w the spigot world
	 * @return the world server of location's world
	 */
	public static Object getWorldServer(World w) {
		try {
			return getObcClass("CraftWorld").getMethod("getHandle").invoke(w);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get protocol version.
	 * <p>
	 * This should work for ALL servers versions <strong>BUT</strong> not at startup of server (only around 5 tickets after). This is not true since MC 1.16.5.
	 * <p>
	 * The "issue" of loading comes from the way to get protocol version.
	 * 
	 * @return the protocol version of 0 if not found
	 */
	public static int getProtocolVersion() {
		try {
			Class<?> sharedConstants = PacketUtils.getNmsClass("SharedConstants", "");
			try { // try get value directly
				return (int) sharedConstants.getDeclaredField("RELEASE_NETWORK_PROTOCOL_VERSION").get(null);
			} catch (Exception e) {}
			return (int) sharedConstants.getDeclaredMethod("c").invoke(null);
		} catch (Exception e) { e.printStackTrace(); } // ignore because it's just an old version
		try {
			Class<?> serverClazz = getNmsClass("MinecraftServer", "server.");
			Object server = serverClazz.getDeclaredMethod("getServer").invoke(null);
			// Grab the ping class and find the field to access it
			Class<?> pingClazz = getNmsClass("ServerPing", "network.protocol.status.");
			Object ping = null;
			for (Field field : serverClazz.getDeclaredFields()) {
				if (field.getType() == pingClazz) {
					field.setAccessible(true);
					ping = field.get(server);
					break;
				}
			}

			// Now get the ServerData inside ServerPing
			Class<?> serverDataClass = getNmsClass("ServerPing$ServerData", "network.protocol.status.");
			Object serverData = ReflectionUtils.getFirstWith(ping, pingClazz, serverDataClass);
	        for (Field field : pingClazz.getDeclaredFields()) {
	            if (field.getType() == serverDataClass) {
	                field.setAccessible(true);
	                serverData = field.get(ping);
	                //break;
	            }
	        }
			// Get protocol version field
			for (Field field : serverDataClass.getDeclaredFields()) {
				if (field.getType() != int.class) {
					continue;
				}

				field.setAccessible(true);
				int protocolVersion = (int) field.get(serverData);
				if (protocolVersion != -1) {
					return protocolVersion;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
