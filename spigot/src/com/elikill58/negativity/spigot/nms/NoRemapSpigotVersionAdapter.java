package com.elikill58.negativity.spigot.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.PacketUtils;

public abstract class NoRemapSpigotVersionAdapter extends SpigotVersionAdapter {

	public Method getPlayerHandle, mathTps;
	public Field pingField, tpsField;
	public Object dedicatedServer;

	public NoRemapSpigotVersionAdapter(String version) {
		super(version);
		try {
			getPlayerHandle = PacketUtils.getObcClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
			pingField = PacketUtils.getNmsClass("EntityPlayer").getDeclaredField("ping");

			Class<?> mathHelperClass = PacketUtils.getNmsClass("MathHelper");
			mathTps = mathHelperClass.getDeclaredMethod("a", long[].class);

			dedicatedServer = PacketUtils.getDedicatedServer();
			
			tpsField = PacketUtils.getNmsClass("MinecraftServer").getDeclaredField(getTpsFieldName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getPlayerPing(Player player) {
		try {
			return pingField.getInt(getPlayerHandle.invoke(player));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public double getAverageTps() {
		try {
			return (double) mathTps.invoke(null, tpsField.get(dedicatedServer));
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public abstract String getTpsFieldName();
}
