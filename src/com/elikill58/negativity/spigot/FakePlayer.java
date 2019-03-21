package com.elikill58.negativity.spigot;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.Utils;
import com.mojang.authlib.GameProfile;

public class FakePlayer {

	// For reflection -- To don't make a lot of time the same request
	private static Class<?> enumPlayerInfo = Utils.getEnumPlayerInfoAction();
	private static Class<?> minecraftServerClass, playerInteractManagerClass;
	private static Constructor<?> entityPlayerConstructor, playerInteractManagerConstructor, packetEntitySpawnConstructor,
				packetEntityDestroyConstructor, packetPlayerInfoConstructor;
	private static Object minecraftServer, playerInfoAddPlayer, playerInfoRemovePlayer;
	
	private Object entityPlayer;
	private Location loc;
	private final GameProfile gameProfile;
	
	public FakePlayer(Location loc, String name) {
	    this(new GameProfile(UUID.fromString("0-0-0-0-0"), name), loc);
	}
	
	public FakePlayer(GameProfile game, Location loc) {
		this.gameProfile = game;
		this.loc = loc;
        try {
			Object worldServerObj = Utils.getWorldServer(loc);
			Object temp = playerInteractManagerConstructor.newInstance(worldServerObj);
			entityPlayer = entityPlayerConstructor.newInstance(minecraftServer, worldServerObj, game, temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public FakePlayer show(Player p) {
		try {
			entityPlayer.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class).invoke(entityPlayer, loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
			Utils.sendPacket(p, packetEntitySpawnConstructor.newInstance(entityPlayer));
			Utils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoAddPlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	    Bukkit.getScheduler().runTaskLater(SpigotNegativity.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					Utils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoRemovePlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
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
			Utils.sendPacket(p, packetPlayerInfoConstructor.newInstance(playerInfoRemovePlayer, ((Iterable<?>) Arrays.asList(entityPlayer))));
			Utils.sendPacket(p, packetEntityDestroyConstructor.newInstance(new int[] {(int) entityPlayer.getClass().getMethod("getId").invoke(entityPlayer)}));
		} catch (Exception e) {
			e.printStackTrace();
		}
		SpigotNegativityPlayer.getNegativityPlayer(p).removeFakePlayer(this);
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public GameProfile getProfile() {
		return gameProfile;
	}
	
	public static void loadClass() {
		try {
	    	minecraftServerClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".MinecraftServer");
	    	playerInteractManagerClass = Class.forName("net.minecraft.server." + Utils.VERSION + ".PlayerInteractManager");
			entityPlayerConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".EntityPlayer").getConstructor(minecraftServerClass, Class.forName("net.minecraft.server." + Utils.VERSION + ".WorldServer"), GameProfile.class, playerInteractManagerClass);
			playerInteractManagerConstructor = playerInteractManagerClass.getConstructor(Class.forName("net.minecraft.server." + Utils.VERSION + ".World"));
			minecraftServer = minecraftServerClass.getMethod("getServer").invoke(minecraftServerClass);
			
			packetEntitySpawnConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutNamedEntitySpawn").getConstructor(Class.forName("net.minecraft.server." + Utils.VERSION + ".EntityHuman"));
			packetEntityDestroyConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutEntityDestroy").getConstructor(int[].class);
			packetPlayerInfoConstructor = Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayOutPlayerInfo").getConstructor(enumPlayerInfo, Iterable.class);

			playerInfoAddPlayer = enumPlayerInfo.getField("ADD_PLAYER").get(enumPlayerInfo);
			playerInfoRemovePlayer = enumPlayerInfo.getField("REMOVE_PLAYER").get(enumPlayerInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
