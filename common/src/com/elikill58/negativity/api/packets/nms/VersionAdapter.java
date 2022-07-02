package com.elikill58.negativity.api.packets.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketHandshake;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.NPacketStatus;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeUnset;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.api.packets.packet.status.NPacketStatusUnset;
import com.elikill58.negativity.universal.Adapter;

@SuppressWarnings("unchecked")
public abstract class VersionAdapter<R> {

	protected final HashMap<String, BiFunction<R, Object, NPacketPlayOut>> packetsPlayOut = new HashMap<>();
	protected final HashMap<String, BiFunction<R, Object, NPacketPlayIn>> packetsPlayIn = new HashMap<>();
	protected final HashMap<String, BiFunction<R, Object, NPacketHandshake>> packetsHandshake = new HashMap<>();
	protected final HashMap<String, BiFunction<R, Object, NPacketStatus>> packetsStatus = new HashMap<>();
	protected final HashMap<PacketType, BiFunction<R, NPacket, Object>> negativityToPlatform = new HashMap<>();
	protected final List<String> unknownPacket = new ArrayList<>();
	protected final String version;
	
	public VersionAdapter(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	private R getR(Player p) {
		return (R) p.getDefault();
	}
	
	protected void log() {
		Adapter.getAdapter().getLogger().info("[Packets-" + version + "] Loaded " + packetsPlayIn.size()
		+ " PlayIn, " + packetsPlayOut.size() + " PlayOut, " + packetsHandshake.size() + " Handshake, " + packetsStatus.size() + " Status and " + negativityToPlatform.size() + " negativity-to-platform.");
	}
	
	public void sendPacket(Player pl, NPacket packet) {
		BiFunction<R, NPacket, Object> packetMaker = negativityToPlatform.get(packet.getPacketType());
		if(packetMaker == null)
			return;
		R p = getR(pl);
		sendPacket(p, packetMaker.apply(p, packet));
	}
	
	public abstract void sendPacket(R p, Object basicPacket);

	
	public void queuePacket(Player pl, NPacket packet) {
		BiFunction<R, NPacket, Object> packetMaker = negativityToPlatform.get(packet.getPacketType());
		if(packetMaker == null)
			return;
		R p = getR(pl);
		queuePacket(p, packetMaker.apply(p, packet));
	}
	
	public abstract void queuePacket(R p, Object basicPacket);

	@Deprecated
	public NPacket getPacket(R player, Object nms, String packetName) {
		try {
			if (packetName.startsWith(PacketType.CLIENT_PREFIX) || packetName.startsWith("Serverbound"))
				return packetsPlayIn.getOrDefault(packetName, (p, obj) -> new NPacketPlayInUnset(packetName)).apply(player, nms);
			else if (packetName.startsWith(PacketType.SERVER_PREFIX) || packetName.startsWith("Clientbound"))
				return packetsPlayOut.getOrDefault(packetName, (p, obj) -> new NPacketPlayOutUnset(packetName)).apply(player, nms);
			else if (packetName.startsWith(PacketType.LOGIN_PREFIX))
				return new NPacketLoginUnset();
			else if (packetName.startsWith(PacketType.STATUS_PREFIX))
				return packetsStatus.getOrDefault(packetName, (p, obj) -> new NPacketStatusUnset()).apply(player, nms);
			else if (packetName.startsWith(PacketType.HANDSHAKE_PREFIX))
				return packetsHandshake.getOrDefault(packetName, (p, obj) -> new NPacketHandshakeUnset()).apply(player, nms);
			else { // name are obfuscated, trying to find it anyway
				if(packetsPlayIn.containsKey(packetName))
					return packetsPlayIn.get(packetName).apply(player, nms);
			}
		} catch (Exception e) {
			Adapter.getAdapter().debug("[VersionAdapter] Failed to manage packet " + packetName + ". NMS: " + nms.getClass().getSimpleName());
			e.printStackTrace();
		}
		if(!unknownPacket.contains(packetName)) { // if wasn't present
			unknownPacket.add(packetName);
			Adapter a = Adapter.getAdapter();
			a.debug("[VersionAdapter] Unknow packet " + packetName + ":");
			for(Field f : nms.getClass().getDeclaredFields()) {
				a.debug(" " + f.getName() + " (type: " + f.getType().getSimpleName() + ")");
			}
			Class<?> superClass = nms.getClass().getSuperclass();
			if(!superClass.equals(Object.class)) {
				a.debug(" SuperClass: " + superClass.getSimpleName());
			}
		}
		return null;
	}

	public NPacket getPacket(Player pl, PacketDirection dir, Object nms) {
		return getPacket(getR(pl), dir, nms);
	}

	public NPacket getPacket(R player, PacketDirection dir, Object nms) {
		return getPacket(player, dir, nms, getNameOfPacket(nms));
	}

	public NPacket getPacket(R player, PacketDirection dir, Object nms, String packetName) {
		try {
			switch (dir) {
			case CLIENT_TO_SERVER:
				return packetsPlayIn.getOrDefault(packetName, (p, obj) -> new NPacketPlayInUnset(packetName)).apply(player, nms);
			case SERVER_TO_CLIENT:
				return packetsPlayOut.getOrDefault(packetName, (p, obj) -> new NPacketPlayOutUnset(packetName)).apply(player, nms);
			case HANDSHAKE:
				return packetsHandshake.getOrDefault(packetName, (p, obj) -> new NPacketHandshakeUnset()).apply(player, nms);
			case LOGIN:
				return new NPacketLoginUnset();
			case STATUS:
				return packetsStatus.getOrDefault(packetName, (p, obj) -> new NPacketStatusUnset()).apply(player, nms);
			}
		} catch (Exception e) {
			Adapter.getAdapter().debug("[VersionAdapter] Failed to manage packet " + packetName + ". NMS: " + nms.getClass().getSimpleName());
			e.printStackTrace();
		}
		if(!unknownPacket.contains(packetName)) { // if wasn't present
			unknownPacket.add(packetName);
			Adapter a = Adapter.getAdapter();
			a.debug("[VersionAdapter] Unknow packet " + packetName + ":");
			for(Field f : nms.getClass().getDeclaredFields()) {
				a.debug(" " + f.getName() + " (type: " + f.getType().getSimpleName() + ")");
			}
			Class<?> superClass = nms.getClass().getSuperclass();
			if(!superClass.equals(Object.class)) {
				a.debug(" SuperClass: " + superClass.getSimpleName());
			}
		}
		return null;
	}
	
	public String getNameOfPacket(Object nms) {
		return nms.getClass().getSimpleName();
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
		return get(obj.getClass(), obj, name);
	}

	protected <T> T get(Class<?> clazz, Object obj, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
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
		return getFromMethod(obj.getClass(), obj, methodName);
	}

	protected <T> T getFromMethod(Class<?> clazz, Object obj, String methodName) {
		try {
			Method f = clazz.getDeclaredMethod(methodName);
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
	
	protected Object callFirstConstructor(Class<?> clazz, Object... args) {
		try {
			Constructor<?> cons = clazz.getConstructors()[0];
			cons.setAccessible(true);
			return cons.newInstance(args);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
