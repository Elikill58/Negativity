package com.elikill58.negativity.api.packets.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;

public abstract class VersionAdapter<R> {

	protected final List<String> unknownPacket = new ArrayList<>();
	protected NamedVersion version;
	
	public VersionAdapter(String version) {
		this.version = Version.getVersion(version).createNamedVersion();
	}
	
	public VersionAdapter(Version version) {
		this.version = version.createNamedVersion();
	}
	
	public NamedVersion getVersion() {
		return version;
	}
	
	private R getR(Player p) {
		return (R) p.getDefault();
	}
	
	public abstract AbstractChannel getPlayerChannel(R p);
	
	public void sendPacket(Player p, NPacket packet) {
		sendPacket(getR(p), packet.create());
	}
	
	public void sendPacket(R p, ByteBuf buf) {
		getPlayerChannel(p).write(buf);
	}
	
	public void queuePacket(Player p, NPacket packet) {
		queuePacket(getR(p), packet.create());
	}
	
	public void queuePacket(R p, ByteBuf buf) {
		getPlayerChannel(p).write(buf);
	}

	public boolean readPacket(Player p, PacketDirection dir, ByteBuf buf) {
		int packetId = new PacketSerializer(buf).readVarInt();
		NPacket packet = version.getPacket(dir, packetId);
		packet.read(new PacketSerializer(buf));
		PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
		EventManager.callEvent(event);
		return event.isCancelled();
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
