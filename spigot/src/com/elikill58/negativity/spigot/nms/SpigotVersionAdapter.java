package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.PacketContent.ContentModifier;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.NPacketStatus;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInListener;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeUnset;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInArmAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity.EnumEntityUseAction;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityTeleport;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutEntityVelocity;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutExplosion;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutPosition;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.item.SpigotItemStack;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

@SuppressWarnings("unchecked")
public abstract class SpigotVersionAdapter {

	protected HashMap<String, BiFunction<Player, Object, NPacketPlayOut>> packetsPlayOut = new HashMap<>();
	protected HashMap<String, BiFunction<Player, Object, NPacketPlayIn>> packetsPlayIn = new HashMap<>();
	protected HashMap<String, BiFunction<Player, Object, NPacketHandshake>> packetsHandshake = new HashMap<>();
	protected HashMap<String, BiFunction<Player, Object, NPacketStatus>> packetsStatus = new HashMap<>();
	private final String version;

	public SpigotVersionAdapter(String version) {
		this.version = version;
		packetsPlayIn.put("PacketPlayInArmAnimation",
				(player, packet) -> new NPacketPlayInArmAnimation(System.currentTimeMillis()));
		packetsPlayIn.put("PacketPlayInChat", (player, packet) -> new NPacketPlayInChat(get(packet, "a")));

		packetsPlayIn.put("PacketPlayInPositionLook", (player, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPositionLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInPosition", (player, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPosition(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"),
						get(f, c, "pitch"), get(f, c, getOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInLook", (player, f) -> {
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
		packetsPlayIn.put("PacketPlayInFlying", (player, f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"),
					get(f, getOnGroundFieldName()), get(f, "hasPos"), get(f, "hasLook"));
		});
		packetsPlayIn.put("PacketPlayInKeepAlive",
				(player, f) -> new NPacketPlayInKeepAlive(new Long(getSafe(f, "a").toString())));
		packetsPlayIn.put("PacketPlayInUseEntity", (player, f) -> {
			Object vec3D = get(f, "c");
			Vector vec = vec3D == null ? new Vector(0, 0, 0) : getVectorFromVec3D(vec3D);
			return new NPacketPlayInUseEntity(get(f, "a"), vec,
					EnumEntityUseAction.valueOf(((Enum<?>) get(f, "action")).name()));
		});
		packetsPlayIn.put("PacketPlayInBlockPlace", (p, packet) -> {
			try {
				PlayerInventory inventory = p.getInventory();
				ItemStack handItem;
				if (getStr(packet, "a").equalsIgnoreCase("MAIN_HAND")) {
					handItem = new SpigotItemStack(inventory.getItemInMainHand());
				} else {
					handItem = new SpigotItemStack(inventory.getItemInOffHand());
				}
				Object player = PacketUtils.getEntityPlayer(p);
				float f1 = get(player, "pitch");
				float f2 = get(player, "yaw");
				double d0 = get(player, "locX");
				double d1 = ((double) get(player, "locY")) + ((double) getFromMethod(player, "getHeadHeight"));
				double d2 = get(player, "locZ");
				Class<?> vec3DClass = PacketUtils.getNmsClass("Vec3D", "world.phys.");
				Object vec3d = vec3DClass.getConstructor(double.class, double.class, double.class).newInstance(d0, d1, d2);
				float f3 = cos(-f2 * 0.017453292F - 3.1415927F);
				float f4 = sin(-f2 * 0.017453292F - 3.1415927F);
				float f5 = -cos(-f1 * 0.017453292F);
				float f6 = sin(-f1 * 0.017453292F);
				float f7 = f4 * f5;
				float f8 = f3 * f5;
				double d3 = (p.getGameMode().equals(GameMode.CREATIVE)) ? 5.0D : 4.5D;
				Object vec3d1 = vec3DClass.getMethod("add", double.class, double.class, double.class).invoke(vec3d, f7 * d3, f6 * d3, f8 * d3);
				Location loc = p.getLocation();
				Object worldServer = PacketUtils.getWorldServer(loc);
				Object movingObj = PacketUtils.getNmsClass("World", "world.level.").getMethod("rayTrace", vec3DClass, vec3DClass).invoke(worldServer, vec3d, vec3d1);
				Object vec = getFromMethod(movingObj, "a");
				return new NPacketPlayInBlockPlace(getFromMethod(vec, "getX"), getFromMethod(vec, "getY"), getFromMethod(vec, "getZ"), handItem,
					new Vector(loc.getX(), loc.getY() + p.getEyeHeight(), loc.getZ()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});

		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, packet) -> {
			Object pos = get(packet, "b");
			return new NPacketPlayOutBlockBreakAnimation(get(pos, "x"), get(pos, "y"), get(pos, "z"), get(packet, "a"),
					get(packet, "c"));
		});
		packetsPlayOut.put("PacketPlayOutKeepAlive",
				(player, f) -> new NPacketPlayOutKeepAlive(new Long(getSafe(f, "a").toString())));
		packetsPlayOut.put("PacketPlayOutEntityTeleport", (player, packet) -> {
			return new NPacketPlayOutEntityTeleport(get(packet, "a"), Double.parseDouble(getStr(packet, "b")),
					Double.parseDouble(getStr(packet, "c")), Double.parseDouble(getStr(packet, "d")),
					Float.parseFloat(getStr(packet, "e")), Float.parseFloat(getStr(packet, "f")), get(packet, "g"));
		});
		packetsPlayOut.put("PacketPlayOutEntityVelocity",
				(p, pa) -> new NPacketPlayOutEntityVelocity(get(pa, "a"), get(pa, "b"), get(pa, "c"), get(pa, "d")));
		packetsPlayOut.put("PacketPlayOutPosition", (p, pa) -> new NPacketPlayOutPosition(get(pa, "a"), get(pa, "b"),
				get(pa, "c"), get(pa, "d"), get(pa, "e")));
		packetsPlayOut.put("PacketPlayOutExplosion", (p, pa) -> new NPacketPlayOutExplosion(get(pa, "a"), get(pa, "b"),
				get(pa, "c"), get(pa, "f"), get(pa, "g"), get(pa, "h")));
		packetsPlayOut.put("PacketPlayOutEntity", (player, packet) -> {
			return new NPacketPlayOutEntity(get(packet, "a"), Double.parseDouble(getStr(packet, "b")),
					Double.parseDouble(getStr(packet, "c")), Double.parseDouble(getStr(packet, "d")));
		});
		
		packetsHandshake.put("PacketHandshakingInListener", (player, t) -> new NPacketHandshakeInListener());
		packetsHandshake.put("PacketHandshakingInSetProtocol", (player, raw) -> {
			PacketContent content = new PacketContent(raw);
			ContentModifier<Integer> ints = content.getIntegers();
			return new NPacketHandshakeInSetProtocol(ints.read("a", 0), content.getStrings().readSafely(0, "0.0.0.0"), ints.read("port", 0));
		});

		SpigotNegativity.getInstance().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size()
				+ " PlayIn, " + packetsPlayOut.size() + " PlayOut, " + packetsHandshake.size() + " Handshake and " + packetsStatus.size() + " Status.");
	}

	protected abstract String getOnGroundFieldName();

	public abstract double getAverageTps();

	public abstract float cos(float f);
	public abstract float sin(float f);

	public List<Player> getOnlinePlayers() {
		List<Player> list = new ArrayList<>();
		try {
			Class<?> mcServer = Class.forName("net.minecraft.server." + VERSION + ".MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			Object craftServer = server.getClass().getField("server").get(server);
			Object getted = craftServer.getClass().getMethod("getOnlinePlayers").invoke(craftServer);
			if (getted instanceof Player[])
				for (Player obj : (Player[]) getted)
					list.add(obj);
			else if (getted instanceof List)
				for (Object obj : (List<?>) getted)
					list.add((Player) obj);
			else
				System.out.println("Unknow getOnlinePlayers");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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

	public void sendPacket(Player p, Object packet) {
		try {
			Object playerConnection = getPlayerConnection(p);
			playerConnection.getClass().getMethod("sendPacket", PacketUtils.getNmsClass("Packet", "network.protocol.game."))
					.invoke(playerConnection, packet);
		} catch (Exception e) {
			e.printStackTrace();
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

	public String getVersion() {
		return version;
	}

	public NPacket getPacket(Player player, Object nms, String packetName) {
		if (packetName.startsWith(PacketType.CLIENT_PREFIX))
			return packetsPlayIn.getOrDefault(packetName, (p, obj) -> new NPacketPlayInUnset(nms.getClass().getName()))
					.apply(player, nms);
		else if (packetName.startsWith(PacketType.SERVER_PREFIX))
			return packetsPlayOut.getOrDefault(packetName, (p, obj) -> new NPacketPlayOutUnset()).apply(player, nms);
		else if (packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		else if (packetName.startsWith(PacketType.STATUS_PREFIX))
			return packetsStatus.getOrDefault(packetName, (p, obj) -> new NPacketStatusUnset()).apply(player, nms);
		else if (packetName.startsWith(PacketType.HANDSHAKE_PREFIX))
			return packetsHandshake.getOrDefault(packetName, (p, obj) -> new NPacketHandshakeUnset()).apply(player, nms);
		Adapter.getAdapter().debug("[SpigotVersionAdapter] Unknow packet " + packetName + ".");
		return null;
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
			default:
				return instance = new Spigot_UnknowVersion(VERSION);
			}
		}
		return instance;
	}

	protected <T> T get(Object obj, Class<?> clazz, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find field " + name + " in class " + obj.getClass().getSimpleName() + " for class " + clazz.getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected <T> T get(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find field " + name + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected Object getSafe(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find safe field " + name + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected String getStr(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj).toString();
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find str field " + name + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected <T> T getFromMethod(Object obj, String methodName) {
		try {
			Method f = obj.getClass().getDeclaredMethod(methodName);
			f.setAccessible(true);
			return (T) f.invoke(obj);
		} catch (NoSuchMethodException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug("Failed to find method " + methodName + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Vector getVectorFromVec3D(Object vec) {
		if(Version.getVersion().isNewerOrEquals(Version.V1_9)) {
			return new Vector((double) get(vec, "x"), (double) get(vec, "x"), (double) get(vec, "x"));
		} else {
			return new Vector((double) get(vec, "a"), (double) get(vec, "b"), (double) get(vec, "c"));
		}
	}
}
