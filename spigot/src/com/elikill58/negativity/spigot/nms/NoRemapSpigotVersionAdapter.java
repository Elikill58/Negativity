package com.elikill58.negativity.spigot.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.spigot.utils.PacketUtils;

public abstract class NoRemapSpigotVersionAdapter extends SpigotVersionAdapter {

	public Method baseBlockGetX, baseBlockGetY, baseBlockGetZ, getPlayerHandle, mathCos, mathSin, mathTps;
	public Field pingField, tpsField;
	public Object dedicatedServer;

	public NoRemapSpigotVersionAdapter(String version) {
		super(version);
		try {
			Class<?> baseBlockClass = PacketUtils.getNmsClass("BaseBlockPosition");
			baseBlockGetX = baseBlockClass.getDeclaredMethod("getX");
			baseBlockGetY = baseBlockClass.getDeclaredMethod("getY");
			baseBlockGetZ = baseBlockClass.getDeclaredMethod("getZ");

			getPlayerHandle = PacketUtils.getObcClass("entity.CraftPlayer").getDeclaredMethod("getHandle");
			pingField = PacketUtils.getNmsClass("EntityPlayer").getDeclaredField("ping");

			Class<?> mathHelperClass = PacketUtils.getNmsClass("MathHelper");
			mathCos = mathHelperClass.getDeclaredMethod("cos", float.class);
			mathSin = mathHelperClass.getDeclaredMethod("sin", float.class);
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
	public BlockPosition getBlockPosition(Object obj) {
		try {
			return new BlockPosition((int) baseBlockGetX.invoke(obj), (int) baseBlockGetY.invoke(obj),
					(int) baseBlockGetZ.invoke(obj));
		} catch (Exception e) {
			e.printStackTrace();
			return new BlockPosition(0, 0, 0);
		}
	}

	@Override
	public float cos(float f) {
		try {
			return (float) mathCos.invoke(null, f);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public float sin(float f) {
		try {
			return (float) mathSin.invoke(null, f);
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
