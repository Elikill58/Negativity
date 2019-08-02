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
	
	public FakePlayer(Location loc, String name) {
		this(loc, name, UUID.fromString("0-0-0-0-0"));
	}
	
	public FakePlayer(Location loc, String name, UUID uuid) {
		this.uuid = uuid;
		this.loc = loc;
        try {
    		this.gameProfile = gameProfileConstructor.newInstance(uuid, name);
			Object worldServerObj = Utils.getWorldServer(loc);
			Object temp = playerInteractManagerConstructor.newInstance(worldServerObj);
			entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServerObj, gameProfile, temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FakePlayer show(Player p) {
		try {
			entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			Object dw = entityPlayer.getClass().getMethod("getDataWatcher").invoke(entityPlayer);
			if(Version.getVersion().isNewerThan(Version.V1_8)) {
				Method setDwMethod = dw.getClass().getMethod("set", Class.forName("net.minecraft.server." + Utils.VERSION + ".DataWatcherObject"), Object.class);
				Object dataWatcherRegistry = Class.forName("net.minecraft.server." + Utils.VERSION + ".DataWatcherRegistry").getDeclaredField("a").get(Class.forName("net.minecraft.server." + Utils.VERSION + ".DataWatcherRegistry"));
				setDwMethod.invoke(dw, dataWatcherRegistry.getClass().getMethod("a", int.class).invoke(dataWatcherRegistry, 0), (Byte) (byte) 0x20);
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
		}, 40);
		return this;
	}
	
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

		if (SpigotNegativityPlayer.contains(p)) {
			SpigotNegativityPlayer.getNegativityPlayer(p).removeFakePlayer(this);
		}
	}
	
	public Location getLocation() {
		return loc;
	}

	public Object getProfile() {
		return getGameProfile();
	}
	
	public Object getGameProfile() {
		return gameProfile;
	}
	
	public UUID getId() {
		return uuid;
	}
	
	public static void loadClass() {
		try {
			gameProfileClass = Class.forName(Version.getVersion().equals(Version.V1_7) ? "net.minecraft.util.com.mojang.authlib.GameProfile" : "com.mojang.authlib.GameProfile");
			gameProfileConstructor = gameProfileClass.getConstructor(UUID.class, String.class);
			
			minecraftServerClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".MinecraftServer");
	    	playerInteractManagerClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".PlayerInteractManager");
	    	entityPlayerConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".EntityPlayer").getConstructor(minecraftServerClass, Class.forName("net.minecraft.server." + Utils.VERSION + ".WorldServer"), gameProfileClass, playerInteractManagerClass);
			playerInteractManagerConstructor = playerInteractManagerClass.getConstructor(Class.forName("net.minecraft.server." + Utils.VERSION + ".World" + (Version.getVersion().equals(Version.V1_14) ? "Server" : "")));
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
