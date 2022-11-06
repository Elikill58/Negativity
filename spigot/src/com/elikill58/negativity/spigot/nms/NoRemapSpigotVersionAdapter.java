package com.elikill58.negativity.spigot.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.inventory.Hand;
import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockDig.DigAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Version;

public abstract class NoRemapSpigotVersionAdapter extends SpigotVersionAdapter {

	public Method baseBlockGetX, baseBlockGetY, baseBlockGetZ, getPlayerHandle, mathCos, mathSin, mathTps, blockCombinedId;
	public Field pingField, tpsField;
	public Object dedicatedServer;

	@SuppressWarnings("rawtypes")
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
			
			Class<?> block = PacketUtils.getNmsClass("Block");
			
			if(v.equals(Version.V1_8)) {
				for(Method m : PacketUtils.getNmsClass("RegistryID").getDeclaredMethods())
					if(m.getName().equalsIgnoreCase("b") && m.getParameterCount() == 1)
						blockCombinedId = m;
			} else {
				blockCombinedId = block.getMethod("getCombinedId", PacketUtils.getNmsClass("IBlockData"));
			}

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
					return new NPacketPlayInUseItem(Hand.getHand(new PacketContent(packet).getSpecificModifier(PacketUtils.getNmsClass("EnumHand")).read(0).toString()));
				});
			}
			/*packetsPlayOut.put("PacketPlayOutSpawnEntity", (player, f) -> {
				return new NPacketPlayOutSpawnEntity(EntityType.UNKNOWN, get(f, "a"), v.isNewerOrEquals(Version.V1_8) ? UUID.randomUUID() : UUID.fromString(get(f, "b")),
						get(f, "c"), get(f, "d"), get(f, "e"));
			});
			packetsPlayOut.put("PacketPlayOutNamedEntitySpawn", (p, f) -> new NPacketPlayOutSpawnPlayer(get(f, "a"), UUID.fromString(get(f, "b")), get(f, "c"), get(f, "d"), get(f, "e")));
			packetsPlayOut.put("PacketPlayOutBlockChange", (p, f) -> new NPacketPlayOutBlockChange(getBlockPosition(get(f, "a")), getBlockStateIdFromRegistry(get(f, "block"))));

			if (v.isNewerOrEquals(Version.V1_16)) {
				packetsPlayOut.put("PacketPlayOutMultiBlockChange", (player, f) -> {
					Object chunkCoords = get(f, "a");
					HashMap<BlockPosition, Long> blocks = new HashMap<>();
					try {
						f.getClass().getMethod("a", BiConsumer.class).invoke(f, (BiConsumer) (pos, data) -> {
							blocks.put(getBlockPosition(pos), getBlockStateIdFromRegistry(get(data, "c")));
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					return new NPacketPlayOutMultiBlockChange(getFromMethod(chunkCoords, "getX"), getFromMethod(chunkCoords, "getZ"), blocks);
				});
			} else {
				packetsPlayOut.put("PacketPlayOutMultiBlockChange", (player, f) -> {
					Object chunkCoords = get(f, "a");
					int chunkX = get(chunkCoords, "x");
					int chunkZ = get(chunkCoords, "z");
					HashMap<BlockPosition, Long> blocks = new HashMap<>();
					Object[] blockDatas = get(f, "b");
					for(Object blockData : blockDatas) {
						blocks.put(getBlockPosition(getFromMethod(blockData, "a")), getBlockStateIdFromRegistry(get(blockData, "c")));
					}
					return new NPacketPlayOutMultiBlockChange((long) chunkX, (long) chunkZ, blocks);
				});
			}*/
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
	
	public long getBlockStateIdFromRegistry(Object obj) {
		try {
			if(Version.getVersion().isNewerOrEquals(Version.V1_9)) {
				return (long) blockCombinedId.invoke(null, obj);
			} else {
				return (long) (int) blockCombinedId.invoke(PacketUtils.getNmsClass("Block").getField("d").get(null), obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public abstract String getTpsFieldName();
}
