package com.elikill58.negativity.api.packets.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;

public abstract class VersionAdapter<R> {

	protected Version version;
	protected NamedVersion namedVersion;
	
	public VersionAdapter() {
		this(Version.HIGHER);
	}
	
	public VersionAdapter(int protocolVersion) {
		this(Version.getVersionByProtocolID(protocolVersion));
	}
	
	public VersionAdapter(Version version) {
		this.version = version;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public NamedVersion getNamedVersion() {
		if(namedVersion == null && version != null) {
			this.namedVersion = version.getNamedVersion();
		}
		return namedVersion;
	}
	
	private R getR(Player p) {
		return (R) p.getDefault();
	}
	
	public abstract AbstractChannel getPlayerChannel(R p);
	
	public void sendPacket(Player p, NPacket packet) {
		sendPacket(getR(p), packet.create(p, p.getPlayerVersion()));
	}
	
	public void sendPacket(R p, ByteBuf buf) {
		getPlayerChannel(p).write(buf);
	}
	
	public void queuePacket(Player p, NPacket packet) {
		queuePacket(getR(p), packet.create(p, p.getPlayerVersion()));
	}
	
	public void queuePacket(R p, ByteBuf buf) {
		getPlayerChannel(p).write(buf);
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
}
