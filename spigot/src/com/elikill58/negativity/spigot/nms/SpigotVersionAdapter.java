package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.SkullMeta;

import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.BlockPosition;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.PacketContent.ContentModifier;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInListener;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInArmAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInEntityAction.EnumPlayerAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPong;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInSteerVehicle;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInTeleportAccept;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseItem;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityEffect;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPing;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

@SuppressWarnings("unchecked")
public abstract class SpigotVersionAdapter extends VersionAdapter<Player> {

	public SpigotVersionAdapter(String version) {
		super(version);
		Version v = Version.getVersion();
		packetsPlayIn.put("PacketPlayInArmAnimation",
				(p, packet) -> new NPacketPlayInArmAnimation(System.currentTimeMillis()));
		packetsPlayIn.put("PacketPlayInChat", (p, packet) -> new NPacketPlayInChat(get(packet, "a")));

		packetsPlayIn.put("PacketPlayInPositionLook", (p, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPositionLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInPosition", (p, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPosition(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInLook", (p, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			// return new NPacketPlayInLook(get(f, "x"), get(f, "y"), get(f, "z"), get(f,
			// "yaw"), get(f, "pitch"));
		});
		packetsPlayIn.put("PacketPlayInFlying", (p, f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"),
					get(f, getOnGroundFieldName()), get(f, "hasPos"), get(f, "hasLook"));
		});
		packetsPlayIn.put("PacketPlayInKeepAlive",
				(p, f) -> new NPacketPlayInKeepAlive(new Long(getSafe(f, "a").toString())));
		packetsPlayIn.put("PacketPlayInUseEntity", (p, f) -> {
			Object vec3D = get(f, "c");
			Vector vec = vec3D == null ? new Vector(0, 0, 0) : getVectorFromVec3D(vec3D);
			return new NPacketPlayInUseEntity(get(f, "a"), vec,
					EnumEntityUseAction.valueOf(((Enum<?>) get(f, "action")).name()));
		});
		packetsPlayIn.put("PacketPlayInEntityAction", (p, f) -> {
			EnumPlayerAction action = EnumPlayerAction.getAction(getStr(f, Version.getVersion().isNewerOrEquals(Version.V1_17) ? "b" : "animation"));
			return new NPacketPlayInEntityAction(get(f, "a"), action, get(f, "c"));
		});
		packetsPlayIn.put("PacketPlayInTransaction", (p, f) -> new NPacketPlayInPong((int) (short) get(f, "b")));
		if(v.isNewerOrEquals(Version.V1_14)) {
			packetsPlayIn.put("PacketPlayInUseItem", (p, f) -> {
				Object movingObj = get(f, "a");
				BlockPosition pos = getBlockPosition(get(movingObj, "c"));
				return new NPacketPlayInUseItem(pos.getX(), pos.getY(), pos.getZ(), BlockFace.valueOf(getStr(movingObj, "b").toUpperCase()), get(f, "timestamp"));
			});
		} else {
			packetsPlayIn.put("PacketPlayInUseItem", (p, f) -> {
				BlockPosition pos = getBlockPosition(get(f, "a"));
				long timestamp = 0l;
				try {
					Field timestampField = f.getClass().getDeclaredField("timestamp");
					timestampField.setAccessible(true);
					timestamp = timestampField.getLong(f);
				} catch (NoSuchFieldException e) {
					// it's an old version of spigot
				} catch (Exception e) {
					e.printStackTrace();
				}
				return new NPacketPlayInUseItem(pos.getX(), pos.getY(), pos.getZ(), BlockFace.valueOf(getStr(f, "b").toUpperCase()), timestamp);
			});
		}
		packetsPlayIn.put("PacketPlayInHeldItemSlot", (p, f) -> new NPacketPlayInHeldItemSlot((int) ReflectionUtils.getField(f, v.isNewerOrEquals(Version.V1_17) ? "a" : "itemInHandIndex")));
		packetsPlayIn.put("PacketPlayInSteerVehicle", (p, f) -> {
			PacketContent c = new PacketContent(f);
			ContentModifier<Float> floats = c.getFloats();
			ContentModifier<Boolean> bools = c.getBooleans();
			return new NPacketPlayInSteerVehicle(floats.readSafely(0, 0f), floats.readSafely(1, 0f), bools.readSafely(0, false), bools.readSafely(1, false));
		});
		if(v.isNewerThan(Version.V1_8)) {
			packetsPlayIn.put("PacketPlayInTeleportAccept", (p, f) -> new NPacketPlayInTeleportAccept((int) ReflectionUtils.getField(f, "a")));
		}
		
		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (p, packet) -> {
			Object pos = get(packet, "b");
			return pos == null ? null : new NPacketPlayOutBlockBreakAnimation(getBlockPosition(pos), get(packet, "a"),
					get(packet, "c"));
		});
		packetsPlayOut.put("PacketPlayOutKeepAlive",
				(p, f) -> new NPacketPlayOutKeepAlive(new Long(getSafe(f, "a").toString())));
		packetsPlayOut.put("PacketPlayOutEntityTeleport", (p, packet) -> {
			return new NPacketPlayOutEntityTeleport(get(packet, "a"), Double.parseDouble(getStr(packet, "b")),
					Double.parseDouble(getStr(packet, "c")), Double.parseDouble(getStr(packet, "d")),
					Float.parseFloat(getStr(packet, "e")), Float.parseFloat(getStr(packet, "f")), get(packet, "g"));
		});
		packetsPlayOut.put("PacketPlayOutEntityVelocity",
				(p, f) -> new NPacketPlayOutEntityVelocity(get(f, "a"), get(f, "b"), get(f, "c"), get(f, "d")));
		packetsPlayOut.put("PacketPlayOutPosition", (p, f) -> new NPacketPlayOutPosition(get(f, "a"), get(f, "b"),
				get(f, "c"), get(f, "d"), get(f, "e")));
		packetsPlayOut.put("PacketPlayOutExplosion", (p, f) -> new NPacketPlayOutExplosion(get(f, "a"), get(f, "b"),
				get(f, "c"), get(f, "f"), get(f, "g"), get(f, "h")));
		packetsPlayOut.put("PacketPlayOutEntity", (p, f) -> {
			return new NPacketPlayOutEntity(get(f, "a"), Double.parseDouble(getStr(f, "b")),
					Double.parseDouble(getStr(f, "c")), Double.parseDouble(getStr(f, "d")));
		});
		packetsPlayOut.put("PacketPlayOutEntityEffect", (p, packet) -> {
			return new NPacketPlayOutEntityEffect(get(packet, "a"), (byte) get(packet, "b"), get(packet, "c"), get(packet, "d"), get(packet, "e"));
		});
		packetsPlayOut.put("PacketPlayOutTransaction", (p, f) -> {
			return new NPacketPlayOutPing((int) (short) get(f, "b"));
		});
		
		packetsHandshake.put("PacketHandshakingInListener", (p, t) -> new NPacketHandshakeInListener());
		packetsHandshake.put("PacketHandshakingInSetProtocol", (p, raw) -> {
			PacketContent content = new PacketContent(raw);
			ContentModifier<Integer> ints = content.getIntegers();
			return new NPacketHandshakeInSetProtocol(ints.read("a", 0), content.getStrings().readSafely(0, "0.0.0.0"), ints.read("port", 0));
		});
		
		negativityToPlatform.put(PacketType.Server.PING, (p, f) -> {
			try {
				NPacketPlayOutPing packet = (NPacketPlayOutPing) f;
				return PacketUtils.getNmsClass("PacketPlayOutTransaction").getConstructor(int.class, short.class, boolean.class)
						.newInstance(0, (short) packet.id, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		});
	}

	protected abstract String getOnGroundFieldName();

	public abstract double getAverageTps();

	public abstract float cos(float f);
	public abstract float sin(float f);

	public List<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	public abstract int getPlayerPing(Player player);

	public Class<?> getEnumPlayerInfoAction() {
		try {
			try {
				return Class.forName("net.minecraft.server." + VERSION + ".EnumPlayerInfoAction");
			} catch (Exception e) {
				for (Class<?> clazz : Class.forName("net.minecraft.server." + VERSION + ".PacketPlayOutPlayerInfo")
						.getDeclaredClasses())
					if (clazz.getName().contains("EnumPlayerInfoAction"))
						return clazz;
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double[] getTps() {
		try {
			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer", "server.");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			return (double[]) server.getClass().getField("recentTps").get(server);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().warn("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
			e.printStackTrace();
			return new double[] { 20, 20, 20 };
		}
	}

	public Object getPlayerConnection(Player p) {
		try {
			Object entityPlayer = PacketUtils.getEntityPlayer(p);
			return entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Channel getPlayerChannel(Player p) {
		try {
			Object playerConnection = getPlayerConnection(p);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			return new PacketContent(networkManager).getSpecificModifier(Channel.class).readSafely(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public List<ChannelFuture> getFuturChannel() {
		try {
			Object mcServer = PacketUtils.getDedicatedServer();
			Object co = ReflectionUtils.getFirstWith(mcServer, PacketUtils.getNmsClass("MinecraftServer", "server."), PacketUtils.getNmsClass("ServerConnection", "server.network."));
			try {
				return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "g");
			} catch (NoSuchFieldException e) {
				return (List<ChannelFuture>) ReflectionUtils.getPrivateField(co, "listeningChannels");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Entity> getEntities(World w){
		return w.getEntities();
	}
	
	public BoundingBox getBoundingBox(Entity et) {
		try {
			Class<?> craftEntityClass = PacketUtils.getObcClass("entity.CraftEntity");
			Object ep = craftEntityClass.getDeclaredMethod("getHandle").invoke(craftEntityClass.cast(et));
			Object bb = ReflectionUtils.getFirstWith(ep, PacketUtils.getNmsClass("Entity", "world.entity."), PacketUtils.getNmsClass("AxisAlignedBB", "world.phys."));
			Class<?> clss = bb.getClass();
			boolean hasMinField = false;
			for(Field f : clss.getFields())
				if(f.getName().equalsIgnoreCase("minX"))
					hasMinField = true;
			if(Version.getVersion().isNewerOrEquals(Version.V1_13) && hasMinField) {
				double minX = clss.getField("minX").getDouble(bb);
				double minY = clss.getField("minY").getDouble(bb);
				double minZ = clss.getField("minZ").getDouble(bb);
				
				double maxX = clss.getField("maxX").getDouble(bb);
				double maxY = clss.getField("maxY").getDouble(bb);
				double maxZ = clss.getField("maxZ").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			} else {
				double minX = clss.getField("a").getDouble(bb);
				double minY = clss.getField("b").getDouble(bb);
				double minZ = clss.getField("c").getDouble(bb);
				
				double maxX = clss.getField("d").getDouble(bb);
				double maxY = clss.getField("e").getDouble(bb);
				double maxZ = clss.getField("f").getDouble(bb);
				
				return new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public abstract BlockPosition getBlockPosition(Object obj);
	
	public org.bukkit.inventory.ItemStack createSkull(OfflinePlayer owner) { // method used by old versions
		// should be "PLAYER_HEAD" and nothing else.
		// can't use direct material else we will have running issue on old versions
		org.bukkit.inventory.ItemStack itemStack = new org.bukkit.inventory.ItemStack((org.bukkit.Material) Materials.PLAYER_HEAD.getDefault());
    	SkullMeta skullmeta = (SkullMeta) (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
		skullmeta.setOwningPlayer(owner); // warn: this method seems to exist since 1.12.1
		return itemStack;
	}

	private static SpigotVersionAdapter instance;

	public static SpigotVersionAdapter getVersionAdapter() {
		if (instance == null) {
			switch (VERSION) {
			case "v1_7_R4":
				return instance = new Spigot_1_7_R4();
			case "v1_8_R3":
				return instance = new Spigot_1_8_R3();
			case "v1_9_R1":
				return instance = new Spigot_1_9_R1();
			case "v1_9_R2":
				return instance = new Spigot_1_9_R2();
			case "v1_10_R1":
				return instance = new Spigot_1_10_R1();
			case "v1_11_R1":
				return instance = new Spigot_1_11_R1();
			case "v1_12_R1":
				return instance = new Spigot_1_12_R1();
			case "v1_13_R2":
				return instance = new Spigot_1_13_R2();
			case "v1_14_R1":
				return instance = new Spigot_1_14_R1();
			case "v1_15_R1":
				return instance = new Spigot_1_15_R1();
			case "v1_16_R1":
				return instance = new Spigot_1_16_R1();
			case "v1_16_R3":
				return instance = new Spigot_1_16_R3();
			case "v1_17_R1":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot17.Spigot_1_17_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case "v1_18_R1":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot18.Spigot_1_18_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case "v1_18_R2":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot18.Spigot_1_18_R2").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			case "v1_19_R1":
				try {
					return instance = (SpigotVersionAdapter) Class
							.forName("com.elikill58.negativity.spigot19.Spigot_1_19_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			default:
				return instance = new Spigot_UnknowVersion(VERSION);
			}
		}
		return instance;
	}

	public Vector getVectorFromVec3D(Object vec) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			return new Vector((double) get(vec, "x"), (double) get(vec, "x"), (double) get(vec, "x"));
		} else {
			return new Vector((double) get(vec, "a"), (double) get(vec, "b"), (double) get(vec, "c"));
		}
	}
	
	@Override
	public void sendPacket(Player p, Object packet) {
		try {
			Object playerConnection = getPlayerConnection(p);
			playerConnection.getClass().getMethod("sendPacket", PacketUtils.getNmsClass("Packet", "network.protocol.game."))
					.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void queuePacket(Player p, Object packet) {
		try {
			Object network = get(getPlayerConnection(p), "networkManager");
			Queue queue = (Queue) get(network, Version.getVersion().isNewerOrEquals(Version.V1_13) ? "packetQueue" : "i");
			queue.add(callFirstConstructor(PacketUtils.getNmsClass("NetworkManager$QueuedPacket"), packet, null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
