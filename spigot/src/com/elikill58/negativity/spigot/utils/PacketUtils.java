package com.elikill58.negativity.spigot.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Version;

public class PacketUtils {

	private static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];

	public static Class<?> CRAFT_PLAYER_CLASS, CRAFT_ENTITY_CLASS;
	public static Class<?> ENUM_PLAYER_INFO = SpigotVersionAdapter.getVersionAdapter().getEnumPlayerInfoAction();
	
	static {
		try {
			CRAFT_PLAYER_CLASS = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
			CRAFT_ENTITY_CLASS = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftEntity");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This Map is to reduce Reflection action which take more ressources than just RAM action
	 */
	private static final HashMap<String, Class<?>> ALL_CLASS = new HashMap<>();
	
	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name of the NMS class (in net.minecraft.server package ONLY, because it's NMS)
	 * @return clazz
	 */
	public static Class<?> getNmsClass(String name){
		return ALL_CLASS.computeIfAbsent(name, (s) -> {
			try {
				return Class.forName("net.minecraft.server." + VERSION + "." + name);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}
	
	/**
	 * Create a new instance of a packet (without any parameters)
	 * 
	 * @param packetName the name of the packet which is in NMS
	 * @return the created packet
	 */
	public static Object createPacket(String packetName) {
		try {
			return getNmsClass(packetName).getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get the current player ping
	 *
	 * @param p the player
	 * @return the player ping
	 */
	public static int getPing(Player p) {
		try {
			Object entityPlayer = getEntityPlayer(p);
			return entityPlayer.getClass().getField("ping").getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
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
			sendPacket(p, getNmsClass(packetName).getConstructor(type).newInstance(data));
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
			Object craftPlayer = CRAFT_PLAYER_CLASS.cast(p);
			return craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get NMS entity player of specified one
	 * 
	 * @param et the player that we want the NMS entity player
	 * @return the entity player
	 */
	public static Object getNMSEntity(Entity et) {
		try {
			Object craftEntity = CRAFT_ENTITY_CLASS.cast(et);
			return craftEntity.getClass().getMethod("getHandle").invoke(craftEntity);
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
			Object object = Class.forName("org.bukkit.craftbukkit." + VERSION + ".CraftWorld").cast(loc.getWorld());
			return object.getClass().getMethod("getHandle").invoke(object);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getNmsEntityName(Object nmsEntity) {
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
				Object chatBaseComponent = getNmsClass("Entity").getDeclaredMethod("getDisplayName").invoke(nmsEntity);
				return (String) getNmsClass("IChatBaseComponent").getDeclaredMethod("getString").invoke(chatBaseComponent);
			} else {
				return (String) getNmsClass("Entity").getDeclaredMethod("getName").invoke(nmsEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getBoundingBox(Entity p) {
		try {
			//((CraftEntity) p).getHandle().getBoundingBox();
			Object cp = CRAFT_ENTITY_CLASS.cast(p);
			Class<?> craftMonsterClass = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftLivingEntity");
			if(cp.getClass().isInstance(craftMonsterClass)) { // prevent protected items
				Object ep = craftMonsterClass.getDeclaredMethod("getHandle").invoke(craftMonsterClass.cast(cp));
				if(Version.getVersion().equals(Version.V1_7))
					return getNmsClass("Entity").getDeclaredField("boundingBox").get(ep);
				else
					return getNmsClass("Entity").getDeclaredMethod("getBoundingBox").invoke(ep);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
