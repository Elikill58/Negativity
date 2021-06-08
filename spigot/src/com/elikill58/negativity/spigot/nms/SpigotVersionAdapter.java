package com.elikill58.negativity.spigot.nms;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

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
import com.elikill58.negativity.spigot.utils.Utils;

public abstract class SpigotVersionAdapter {
	
	protected HashMap<String, Function<Object, NPacketPlayOut>> packetsPlayOut = new HashMap<String, Function<Object, NPacketPlayOut>>();
	protected HashMap<String, Function<Object, NPacketPlayIn>> packetsPlayIn = new HashMap<String, Function<Object, NPacketPlayIn>>();
	private final String version;
	
	public SpigotVersionAdapter(String version) {
		this.version = version;
		packetsPlayIn.put("PacketPlayInArmAnimation", (packet) -> new NPacketPlayInArmAnimation(System.currentTimeMillis()));
		packetsPlayIn.put("PacketPlayInChat", (packet) -> new NPacketPlayInChat(get(packet, "a")));

		packetsPlayIn.put("PacketPlayInPositionLook", (f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPositionLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"), get(f, c, "pitch"), get(f, c, "f"));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInPosition", (f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInPosition(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"), get(f, c, "pitch"), get(f, c, "f"));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
		packetsPlayIn.put("PacketPlayInLook", (f) -> {
			try {
				Class<?> c = f.getClass().getSuperclass();
				return new NPacketPlayInLook(get(f, c, "x"), get(f, c, "y"), get(f, c, "z"), get(f, c, "yaw"), get(f, c, "pitch"), get(f, c, "f"));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			//return new NPacketPlayInLook(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"));
		});
		packetsPlayIn.put("PacketPlayInFlying", (f) -> {
			return new NPacketPlayInFlying(get(f, "x"), get(f, "y"), get(f, "z"), get(f, "yaw"), get(f, "pitch"), get(f, "f"), get(f, "hasPos"), get(f, "hasLook"));
		});
		packetsPlayIn.put("PacketPlayInKeepAlive", (f) -> new NPacketPlayInKeepAlive(new Long(getSafe(f, "a").toString())));
		

		packetsPlayOut.put("PacketPlayOutBlockBreakAnimation", (packet) -> {
			Object pos = get(packet, "b");
			return new NPacketPlayOutBlockBreakAnimation(get(pos, "x"), get(pos, "y"), get(pos, "z"), get(packet, "a"), get(packet, "c"));
		});
		packetsPlayOut.put("PacketPlayOutKeepAlive", (f) -> new NPacketPlayOutKeepAlive(new Long(getSafe(f, "a").toString())));
		
		SpigotNegativity.getInstance().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size() + " PlayIn and " + packetsPlayOut.size() + " PlayOut.");
	}
	
	public String getVersion() {
		return version;
	}
	
	public NPacket getPacket(Object nms, String packetName) {
		if(packetName.startsWith(PacketType.CLIENT_PREFIX))
			return packetsPlayIn.getOrDefault(packetName, (obj) -> new NPacketPlayInUnset()).apply(nms);
		if(packetName.startsWith(PacketType.SERVER_PREFIX))
			return packetsPlayOut.getOrDefault(packetName, (obj) -> new NPacketPlayOutUnset()).apply(nms);
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
			switch (Utils.VERSION) {
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
			default:
				return instance = new Spigot_UnknowVersion(Utils.VERSION);
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
