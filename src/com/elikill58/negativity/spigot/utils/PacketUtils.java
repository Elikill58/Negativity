package com.elikill58.negativity.spigot.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

public class PacketUtils {

	public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",")
			.split(",")[3];
	public static final String NMS_PREFIX;

	public static Class<?> CRAFT_PLAYER_CLASS, CRAFT_SERVER_CLASS, CRAFT_ENTITY_CLASS;
	public static Class<?> ENUM_PLAYER_INFO;
	
	/**
	 * This Map is to reduce Reflection action which take more ressources than just RAM action
	 */
	private static final HashMap<String, Class<?>> ALL_CLASS = new HashMap<>();
	
	static {
		NMS_PREFIX = Version.getVersion(VERSION).isNewerOrEquals(Version.V1_17) ? "net.minecraft." : "net.minecraft.server." + VERSION + ".";
		try {
			ENUM_PLAYER_INFO = getEnumPlayerInfoAction();
			CRAFT_PLAYER_CLASS = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftPlayer");
			CRAFT_SERVER_CLASS = Class.forName("org.bukkit.craftbukkit." + VERSION + ".CraftServer");
			CRAFT_ENTITY_CLASS = Class.forName("org.bukkit.craftbukkit." + VERSION + ".entity.CraftEntity");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get the Class in NMS, with a processing reducer
	 * 
	 * @param name of the NMS class (in net.minecraft.server package ONLY, because it's NMS)
	 * @return clazz the searched class
	 */
	public static Class<?> getNmsClass(String name, String packagePrefix){
		if(ALL_CLASS.containsKey(name))
			return ALL_CLASS.get(name);
		try {
			Class<?> clazz = Class.forName(NMS_PREFIX + (Version.getVersion(VERSION).isNewerOrEquals(Version.V1_17) ? packagePrefix : "") + name);
			ALL_CLASS.put(name, clazz);
			return clazz;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Create a new instance of a packet (without any parameters)
	 * 
	 * @param packetName the name of the packet which is in NMS
	 * @return the created packet
	 */
	public static Object createPacket(String packetName) {
		try {
			return getNmsClass(packetName, "network.protocol.game.").getConstructor().newInstance();
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
		try {
			Object playerConnection = getPlayerConnection(p);
			playerConnection.getClass().getMethod("sendPacket", getNmsClass("Packet", "network.protocol.")).invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get NMS player connection of specified player
	 * 
	 * @param p Player of which we want to get the player connection
	 * @return the NMS player connection
	 */
	public static Object getPlayerConnection(Player p) {
		try {
			Object entityPlayer = getEntityPlayer(p);
			if(Version.getVersion().isNewerOrEquals(Version.V1_17))
				return entityPlayer.getClass().getField("b").get(entityPlayer);
			else
				return entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
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
	 * @param p the player that we want the NMS entity player
	 * @return the entity player
	 */
	public static Object getCraftServer() {
		try {
			Object craftServer = CRAFT_SERVER_CLASS.cast(Bukkit.getServer());
			return craftServer.getClass().getMethod("getHandle").invoke(craftServer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Get NMS entity player of specified one
	 * 
	 * @param p the player that we want the NMS entity player
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
	 * Get the EnumPlayerInfoAction class which depend of packet PacketPlayOutPlayerInfo
	 * 
	 * @return the class of EnumPlayerInfoAction
	 * @throws ClassNotFoundException 
	 * @throws SecurityException 
	 */
	private static Class<?> getEnumPlayerInfoAction() throws SecurityException, ClassNotFoundException {
		try {
			return getNmsClass("PacketPlayOutPlayerInfo$EnumPlayerInfoAction", "network.protocol.game.");
		} catch (Exception e) {
			for(Class<?> clazz : getNmsClass("PacketPlayOutPlayerInfo", "network.protocol.game.").getDeclaredClasses())
				if(clazz.getName().contains("EnumPlayerInfoAction"))
					return clazz;
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
				Object chatBaseComponent = getNmsClass("Entity", "world.entity.").getDeclaredMethod("getDisplayName").invoke(nmsEntity);
				return (String) getNmsClass("IChatBaseComponent", "network.chat.").getDeclaredMethod("getString").invoke(chatBaseComponent);
			} else {
				return (String) getNmsClass("Entity", "world.entity.").getDeclaredMethod("getName").invoke(nmsEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getBoundingBox(Entity p) {
		try {
			Object ep = CRAFT_ENTITY_CLASS.getDeclaredMethod("getHandle").invoke(CRAFT_ENTITY_CLASS.cast(p));
			return ReflectionUtils.getFirstWith(ep, getNmsClass("Entity", "world.entity."), getNmsClass("AxisAlignedBB", "world.phys."));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
