package com.elikill58.negativity.spigot.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.elikill58.negativity.spigot.utils.PacketUtils;

public abstract class NoRemapSpigotVersionAdapter extends SpigotVersionAdapter {

	public Method mathTps;
	public Field tpsField;
	public Object dedicatedServer;

	public NoRemapSpigotVersionAdapter(int protocolVersion) {
		super(protocolVersion);
		try {
			Class<?> mathHelperClass = PacketUtils.getNmsClass("MathHelper");
			mathTps = mathHelperClass.getDeclaredMethod("a", long[].class);

			dedicatedServer = PacketUtils.getDedicatedServer();
			
			tpsField = PacketUtils.getNmsClass("MinecraftServer").getDeclaredField(getTpsFieldName());
		} catch (Exception e) {
			e.printStackTrace();
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
