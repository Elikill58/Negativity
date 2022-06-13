package com.elikill58.negativity.spigot.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Version;

public class PacketUtils {

	private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];
	public static final String NMS_PREFIX = Version.getVersion(VERSION).isNewerOrEquals(Version.V1_17) ? "net.minecraft." : "net.minecraft.server." + VERSION + ".";
	public static final Class<?> ENUM_PLAYER_INFO = SpigotVersionAdapter.getVersionAdapter().getEnumPlayerInfoAction();
	
	/**
	 * This Map is to reduce Reflection action which take more resources than just RAM action
	 */
	private static final HashMap<String, Class<?>> ALL_CLASS = new HashMap<>();
	
	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name of the NMS class (in net.minecraft.server package ONLY, because it's NMS)
	 * @return the loaded or cached class
	 */
	public static Class<?> getNmsClass(String name){
		return getNmsClass(name, "");
	}
	
	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name of the NMS class (in net.minecraft.server package ONLY, because it's NMS)
	 * @param packagePrefix the prefix of the package for 1.17+
	 * @return the loaded or cached class
	 */
	public static Class<?> getNmsClass(String name, String packagePrefix){
		synchronized(ALL_CLASS) {
			return ALL_CLASS.computeIfAbsent(name, (s) -> {
				try {
					return Class.forName(NMS_PREFIX + (Version.getVersion(VERSION).isNewerOrEquals(Version.V1_17) ? packagePrefix : "") + name);
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
	 * @param name of the OBC class (in org.bukkit.craftbukkit.version. package ONLY, because it's OBC)
	 * @return the loaded or cached class
	 */
	public static Class<?> getObcClass(String name){
		synchronized(ALL_CLASS) {
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
	 * Create and send a packet (with only one parameter)
	 * 
	 * @param p the player which will receive the packet
	 * @param packetName the name of the packet that will be created and sent
	 * @param type the constructor type of parameter
	 * @param data the data associated with parameter type
	 */
	public static void sendPacket(Player p, String packetName, Class<?> type, Object data) {
		try {
			sendPacket(p, getNmsClass(packetName, "network.protocol.game.").getConstructor(type).newInstance(data));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send the packet to the specified player
	 * 
	 * @param p which will receive the packet
	 * @param packet the packet to sent
	 */
	public static void sendPacket(Player p, Object packet) {
		SpigotVersionAdapter.getVersionAdapter().sendPacket(p, packet);
	}
	
	/**
	 * Get NMS entity player of specified one
	 * 
	 * @param p the player that we want the NMS entity player
	 * @return the entity player
	 */
	public static Object getEntityPlayer(Player p) {
		try {
			Object craftPlayer = getObcClass("entity.CraftPlayer").cast(p);
			return craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
		} catch (Exception e) {
			e.printStackTrace();
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
			Object server = getObcClass("CraftServer").cast(Bukkit.getServer());
			return server.getClass().getMethod("getServer").invoke(server);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Get the NMS world server
	 * 
	 * @param loc the location in the world of which we want to get the NMS world server
	 * @return the world server of location's world
	 */
	public static Object getWorldServer(Location loc) {
		try {
			Object object = getObcClass("CraftWorld").cast(loc.getWorld());
			return object.getClass().getMethod("getHandle").invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
