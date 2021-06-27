package com.elikill58.negativity.spigot.nms;

import static com.elikill58.negativity.spigot.utils.Utils.VERSION;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.packets.PacketContent;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInArmAnimation;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInChat;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInKeepAlive;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPosition;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutBlockBreakAnimation;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutKeepAlive;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.Adapter;

import io.netty.channel.Channel;

public abstract class SpigotVersionAdapter {
	
	protected HashMap<String, BiFunction<Player, Object, NPacketPlayOut>> packetsPlayOut = new HashMap<>();
	protected HashMap<String, BiFunction<Player, Object, NPacketPlayIn>> packetsPlayIn = new HashMap<>();
	private final String version;
	
	public SpigotVersionAdapter(String version) {
		this.version = version;
		packetsPlayIn.put("PacketPlayInArmAnimation", (player, packet) -> new NPacketPlayInArmAnimation(System.currentTimeMillis()));
		packetsPlayIn.put("PacketPlayInChat", (player, packet) -> new NPacketPlayInChat(get(packet, "a")));

		packetsPlayIn.put("PacketPlayInPositionLook", (player, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPositionLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"), get(f, c, "pitch"), get(f, c, isOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInPosition", (player, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPosition(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"), get(f, c, "pitch"), get(f, c, isOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInLook", (player, f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"), get(f, c, "pitch"), get(f, c, isOnGroundFieldName()));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			//return new NPacketPlayInLook(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"));
		});
		packetsPlayIn.put("PacketPlayInFlying", (player, f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"), get(f, isOnGroundFieldName()), get(f, "hasPos"), get(f, "hasLook"));
		});
		packetsPlayIn.put("PacketPlayInKeepAlive", (player, f) -> new NPacketPlayInKeepAlive(new Long(getSafe(f, "a").toString())));
		

		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (player, packet) -> {
			Object pos = get(packet, "b");
			return new NPacketPlayOutBlockBreakAnimation(get(pos, "x"), get(pos, "y"), get(pos, "z"), get(packet, "a"), get(packet, "c"));
		});
		packetsPlayOut.put("PacketPlayOutKeepAlive", (player, f) -> new NPacketPlayOutKeepAlive(new Long(getSafe(f, "a").toString())));
		
		SpigotNegativity.getInstance().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size() + " PlayIn and " + packetsPlayOut.size() + " PlayOut.");
	}
	
	protected abstract String isOnGroundFieldName();
	
	public abstract double getAverageTps();
	
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
				for(Class<?> clazz : Class.forName("net.minecraft.server." + VERSION + ".PacketPlayOutPlayerInfo").getDeclaredClasses())
					if(clazz.getName().contains("EnumPlayerInfoAction"))
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
			Class<?> mcServer = PacketUtils.getNmsClass("MinecraftServer");
			Object server = mcServer.getMethod("getServer").invoke(mcServer);
			return (double[]) server.getClass().getField("recentTps").get(server);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().warn("Cannot get TPS (Work on Spigot but NOT CraftBukkit).");
			e.printStackTrace();
			return new double[] {20, 20, 20};
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
			playerConnection.getClass().getMethod("sendPacket", PacketUtils.getNmsClass("Packet")).invoke(playerConnection, packet);
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
	
	public String getVersion() {
		return version;
	}
	
	public NPacket getPacket(Player player, Object nms, String packetName) {
		if(packetName.startsWith(PacketType.CLIENT_PREFIX))
			return packetsPlayIn.getOrDefault(packetName, (p, obj) -> new NPacketPlayInUnset()).apply(player, nms);
		if(packetName.startsWith(PacketType.SERVER_PREFIX))
			return packetsPlayOut.getOrDefault(packetName, (p, obj) -> new NPacketPlayOutUnset()).apply(player, nms);
		if(packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		if(packetName.startsWith(PacketType.STATUS_PREFIX))
			return new NPacketStatusUnset();
		SpigotNegativity.getInstance().getLogger().info("Unknow packet " + packetName + ".");
		return null;
	}
	
	private static SpigotVersionAdapter instance;
	
	public static SpigotVersionAdapter getVersionAdapter() {
		if(instance == null) {
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
					return instance = (SpigotVersionAdapter) Class.forName("com.elikill58.negativity.spigot17.Spigot_1_17_R1").getConstructor().newInstance();
				} catch (ReflectiveOperationException e) {
					throw new RuntimeException(e);
				}
			default:
				return instance = new Spigot_UnknowVersion(VERSION);
			}
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T get(Object obj, Class<?> clazz, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T get(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			/*Field f = null;
			Class<?> searchClass = obj.getClass();
			while(f == null) {
				try {
					f = searchClass.getDeclaredField(name);
					// if field find, end of while
				} catch (NoSuchFieldException e) {
					// not found, get error
					if(searchClass.getSuperclass().equals(Object.class)) {
						SpigotNegativity.getInstance().getLogger().info("[SVA] Class " + searchClass.getName() + " is superclassed by Object.");
						return null;
					} else {
						searchClass = searchClass.getSuperclass();
					}
				}
			}*/
			f.setAccessible(true);
			return (T) f.get(obj);
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
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
