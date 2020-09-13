package com.elikill58.negativity.spigot;

import static com.elikill58.negativity.spigot.utils.PacketUtils.ENUM_PLAYER_INFO;
import static com.elikill58.negativity.spigot.utils.PacketUtils.getNmsClass;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Version;

public class FakePlayer {

	// For reflection -- To don't make a lot of time the same request
	private static Class<?> gameProfileClass;
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
			Object worldServerObj = PacketUtils.getWorldServer(loc);
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
				Class<?> dwRegistryClass = getNmsClass("DataWatcherRegistry");
				Object dwByteSerializer = dwRegistryClass.getDeclaredField("a").get(dwRegistryClass);

				Method dwByteSerializerCreate = dwByteSerializer.getClass().getMethod("a", int.class);
				dwByteSerializerCreate.setAccessible(true);
				Object dwObject = dwByteSerializerCreate.invoke(dwByteSerializer, 0);

				Class<?> dataWatcherObjectClass = getNmsClass("DataWatcherObject");
				Method setDwMethod = dw.getClass().getMethod("set", dataWatcherObjectClass, Object.class);
				setDwMethod.invoke(dw, dwObject, (byte) 0x20);
			} else {
				dw.getClass().getMethod("watch", int.class, Object.class).invoke(dw, 0, (Byte) (byte) 0x20);
			}
	        Object bukkitEntity = entityPlayer.getClass().getMethod("getBukkitEntity").invoke(entityPlayer);
	        PacketUtils.sendPacket(p, packetEntityMetadataConstructor.newInstance(bukkitEntity.getClass().getMethod("getEntityId").invoke(bukkitEntity), dw, true));
	        PacketUtils.sendPacket(p, packetEntitySpawnConstructor.newInstance(entityPlayer));
			if(Version.getVersion().equals(Version.V1_7)) {
				getNmsClass("PacketPlayOutPlayerInfo").getMethod("addPlayer", entityPlayer.getClass())
						.invoke(getNmsClass("PacketPlayOutPlayerInfo"), entityPlayer);
			} else {
				PacketUtils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoAddPlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					if(Version.getVersion().equals(Version.V1_7)) {
						getNmsClass("PacketPlayOutPlayerInfo").getMethod("removePlayer", entityPlayer.getClass())
								.invoke(getNmsClass("PacketPlayOutPlayerInfo"), entityPlayer);
					} else {
						PacketUtils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoRemovePlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 3);
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
				getNmsClass("PacketPlayOutPlayerInfo").getMethod("removePlayer", entityPlayer.getClass())
							.invoke(getNmsClass("PacketPlayOutPlayerInfo"), entityPlayer);
			} else {
				PacketUtils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoRemovePlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
			}
			PacketUtils.sendPacket(p, packetEntityDestroyConstructor.newInstance(new int[] {(int) entityPlayer.getClass().getMethod("getId").invoke(entityPlayer)}));
		} catch (Exception e) {
			e.printStackTrace();
		}

		NegativityPlayer nPlayer = NegativityPlayer.getCached(p.getUniqueId());
		if (nPlayer != null) {
			//nPlayer.removeFakePlayer(this, false);
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
		// run it async to remove loading time on main thread
		CompletableFuture.runAsync(() -> {
			try {
				gameProfileClass = Class.forName(Version.getVersion().equals(Version.V1_7) ? "net.minecraft.util.com.mojang.authlib.GameProfile" : "com.mojang.authlib.GameProfile");
				gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
				
				Class<?> mcSrvClass = getNmsClass("MinecraftServer"), worldSrvClass = getNmsClass("WorldServer");
		    	entityPlayerConstructor = getNmsClass("EntityPlayer").getConstructor(mcSrvClass, getNmsClass("WorldServer"), gameProfileClass, getNmsClass("PlayerInteractManager"));
				playerInteractManagerConstructor = getNmsClass("PlayerInteractManager").getConstructor((Version.getVersion().isNewerOrEquals(Version.V1_14) ? worldSrvClass : getNmsClass("World")));
				minecraftServer = mcSrvClass.getMethod("getServer").invoke(mcSrvClass);
				
				packetEntityMetadataConstructor = getNmsClass("PacketPlayOutEntityMetadata").getConstructor(int.class, getNmsClass("DataWatcher"), boolean.class);
				packetEntitySpawnConstructor = getNmsClass("PacketPlayOutNamedEntitySpawn").getConstructor(getNmsClass("EntityHuman"));
				packetEntityDestroyConstructor = getNmsClass("PacketPlayOutEntityDestroy").getConstructor(int[].class);
				if(!Version.getVersion().equals(Version.V1_7)) {
					packetPlayerInfoConstructor = getNmsClass("PacketPlayOutPlayerInfo").getConstructor(ENUM_PLAYER_INFO, Iterable.class);
					playerInfoAddPlayer = ENUM_PLAYER_INFO.getField("ADD_PLAYER").get(ENUM_PLAYER_INFO);
					playerInfoRemovePlayer = ENUM_PLAYER_INFO.getField("REMOVE_PLAYER").get(ENUM_PLAYER_INFO);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
