package com.elikill58.negativity.spigot.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

public abstract class NoRemapSpigotVersionAdapter extends SpigotVersionAdapter {

	public Method baseBlockGetX, baseBlockGetY, baseBlockGetZ, getPlayerHandle, mathCos, mathSin, mathTps;
	public Field pingField, tpsField;
	public Object dedicatedServer;

	public NoRemapSpigotVersionAdapter(String version) {
		super(version);
		Version v = Version.getVersion(version);
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

			if (v.isNewerOrEquals(Version.V1_13)) {
				packetsPlayIn.put("PacketPlayInBlockDig", (player, packet) -> {
					BlockPosition pos = getBlockPosition(getFromMethod(packet, "b"));
					return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(),
							DigAction.getById(((Enum<?>) getFromMethod(packet, "d")).ordinal()),
							BlockFace.getById(((Enum<?>) getFromMethod(packet, "c")).ordinal()));
				});
			} else {
				packetsPlayIn.put("PacketPlayInBlockDig", (player, packet) -> {
					BlockPosition pos = getBlockPosition(getFromMethod(packet, "a"));
					return new NPacketPlayInBlockDig(pos.getX(), pos.getY(), pos.getZ(),
							DigAction.getById(((Enum<?>) getFromMethod(packet, "c")).ordinal()),
							BlockFace.getById(((Enum<?>) getFromMethod(packet, "b")).ordinal()));
				});
			}
			if (v.isNewerOrEquals(Version.V1_9)) {
				packetsPlayIn.put("PacketPlayInBlockPlace", (p, packet) -> {
					try {
						Object hand = ReflectionUtils.getFirstWith(packet, packet.getClass(),
								PacketUtils.getNmsClass("EnumHand"));
						return new NPacketPlayInUseItem(Hand.getHand(hand.toString()));
					} catch (Exception e) {
						e.printStackTrace();
					}

					return new NPacketPlayInUseItem(Hand.MAIN);
				});
			}
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
