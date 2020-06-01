package com.elikill58.negativity.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Version;

public class FakePlayer {

	// For reflection -- To don't make a lot of time the same request
	private static Class<?> enumPlayerInfo = Utils.getEnumPlayerInfoAction();
	private static Class<?> minecraftServerClass, playerInteractManagerClass, gameProfileClass, playOutPlayerInfo, dataWatcherClass;
	private static Constructor<?> entityPlayerConstructor, playerInteractManagerConstructor, packetEntitySpawnConstructor,
				packetEntityDestroyConstructor, packetPlayerInfoConstructor, gameProfileConstructor, packetEntityMetadataConstructor;
	private static Object minecraftServer, playerInfoAddPlayer, playerInfoRemovePlayer;
	
	private Object entityPlayer, gameProfile;
	private Location loc;
	private UUID uuid;
	private int id;
	
	/**
	 * Create a new fake player
	 * 
	 * @param loc the fake player's location
	 * @param name the fake player name
	 */
	public FakePlayer(Location loc, String name) {
		this(loc, name, UUID.fromString("0-0-0-0-0"));
	}
	
	/**
	 * Create a new fake player
	 * 
	 * @param loc the fake player's location
	 * @param name the fake player name
	 * @param uuid the fake player's uuid
	 */
	public FakePlayer(Location loc, String name, UUID uuid) {
		this.uuid = uuid;
		this.loc = loc;
        try {
    		this.gameProfile = gameProfileConstructor.newInstance(uuid, name);
			Object worldServerObj = Utils.getWorldServer(loc);
			Object temp = playerInteractManagerConstructor.newInstance(worldServerObj);
			entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServerObj, gameProfile, temp);
			id = (int) entityPlayer.getClass().getMethod("getId").invoke(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Show the fake player to the specified online player
	 * 
	 * @param p THe player who will see the entity
	 * @return this
	 */
	public FakePlayer show(Player p) {
		// We don't load chunk, but we cannot spawn entity on no-loaded area
		if(!loc.getChunk().isLoaded())
			return this;
		try {
			entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			Object dw = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);
			if(Version.getVersion().isNewerThan(Version.V1_8)) {
				Class<?> dwRegistryClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".DataWatcherRegistry");
				Object dwByteSerializer = dwRegistryClass.getDeclaredField("a").get(dwRegistryClass);

				Method dwByteSerializerCreate = dwByteSerializer.getClass().getMethod("a", int.class);
				dwByteSerializerCreate.setAccessible(true);
				Object dwObject = dwByteSerializerCreate.invoke(dwByteSerializer, 0);

				Class<?> dataWatcherObjectClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".DataWatcherObject");
				Method setDwMethod = dw.getClass().getMethod("set", dataWatcherObjectClass, Object.class);
				setDwMethod.invoke(dw, dwObject, (byte) 0x20);
			} else {
				dw.getClass().getMethod("watch", int.class, Object.class).invoke(dw, 0, (Byte) (byte) 0x20);
			}
	        Object bukkitEntity = entityPlayer.getClass().getMethod("getBukkitEntity").invoke(entityPlayer);
	        Utils.sendPacket(p, packetEntityMetadataConstructor.newInstance(bukkitEntity.getClass().getMethod("getEntityId").invoke(bukkitEntity), dw, true));
	        Utils.sendPacket(p, packetEntitySpawnConstructor.newInstance(entityPlayer));
			if(Version.getVersion().equals(Version.V1_7)) {
				playOutPlayerInfo.getMethod("addPlayer", entityPlayer.getClass()).invoke(playOutPlayerInfo, entityPlayer);
			} else {
				Utils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoAddPlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					if(Version.getVersion().equals(Version.V1_7)) {
						playOutPlayerInfo.getMethod("removePlayer", entityPlayer.getClass()).invoke(playOutPlayerInfo, entityPlayer);
					} else {
						Utils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoRemovePlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 1);
	    Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				hide(p);
			}
		}, 5);
		return this;
	}
	
	/**
	 * Hide the fake player to the specified online player
	 * 
	 * @param p The player that will not see it
	 */
	public void hide(Player p) {
		try {
			if(Version.getVersion().equals(Version.V1_7)) {
				playOutPlayerInfo.getMethod("removePlayer", entityPlayer.getClass()).invoke(playOutPlayerInfo, entityPlayer);
			} else {
				Utils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoRemovePlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
			}
			Utils.sendPacket(p, packetEntityDestroyConstructor.newInstance(new int[] {(int) entityPlayer.getClass().getMethod("getId").invoke(entityPlayer)}));
		} catch (Exception e) {
			e.printStackTrace();
		}

		SpigotNegativityPlayer nPlayer = SpigotNegativityPlayer.getCached(p.getUniqueId());
		if (nPlayer != null) {
			nPlayer.removeFakePlayer(this, false);
		}
	}
	
	/**
	 *  Get the entity ID of the fake player.
	 *  Alone method to check entity
	 * 
	 * @return the entity ID
	 */
	public int getEntityId() {
		return id;
	}
	
	/**
	 * Get the NMS entity player, but as object for compatibility
	 * 
	 * @return NMS entity player
	 */
	public Object getEntityPlayer() {
		return entityPlayer;
	}
	
	/**
	 * Spawn location of fake player
	 * 
	 * @return the fake player location
	 */
	public Location getLocation() {
		return loc;
	}

	/**
	 * Get the NMS game profile as object for compatibility
	 * 
	 * @return the NMS game profile
	 */
	public Object getProfile() {
		return getGameProfile();
	}

	/**
	 * Get the NMS game profile as object for compatibility
	 * 
	 * @return the NMS game profile
	 */
	public Object getGameProfile() {
		return gameProfile;
	}
	
	/**
	 * Get Unique ID of the fake player
	 * 
	 * @return the player's uuid
	 */
	public UUID getUUID() {
		return uuid;
	}
	
	/**
	 * Called at startup.
	 * 
	 * Load all reflection class for optimization
	 */
	public static void loadClass() {
		try {
			gameProfileClass = Class.forName(Version.getVersion().equals(Version.V1_7) ? "net.minecraft.util.com.mojang.authlib.GameProfile" : "com.mojang.authlib.GameProfile");
			gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
			
			minecraftServerClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".MinecraftServer");
	    	playerInteractManagerClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".PlayerInteractManager");
	    	entityPlayerConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".EntityPlayer").getConstructor(minecraftServerClass, Class.forName("net.minecraft.server." + Utils.VERSION + ".WorldServer"), gameProfileClass, playerInteractManagerClass);
			playerInteractManagerConstructor = playerInteractManagerClass.getConstructor(Class.forName("net.minecraft.server." + Utils.VERSION + ".World" + (Version.getVersion().isNewerOrEquals(Version.V1_14) ? "Server" : "")));
			minecraftServer = minecraftServerClass.getMethod("getServer").invoke(minecraftServerClass);
			
			dataWatcherClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".DataWatcher");
			packetEntityMetadataConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutEntityMetadata").getConstructor(int.class, dataWatcherClass, boolean.class);
			packetEntitySpawnConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutNamedEntitySpawn").getConstructor(Class.forName("net.minecraft.server." + Utils.VERSION + ".EntityHuman"));
			packetEntityDestroyConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutEntityDestroy").getConstructor(int[].class);
			playOutPlayerInfo = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutPlayerInfo");
			if(!Version.getVersion().equals(Version.V1_7)) {
				packetPlayerInfoConstructor = playOutPlayerInfo.getConstructor(enumPlayerInfo, Iterable.class);
				playerInfoAddPlayer = enumPlayerInfo.getField("ADD_PLAYER").get(enumPlayerInfo);
				playerInfoRemovePlayer = enumPlayerInfo.getField("REMOVE_PLAYER").get(enumPlayerInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
