package com.elikill58.negativity.api.packets.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.logger.Debug;

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
	
	public R getR(Player p) {
		return (R) p.getDefault();
	}
	
	public abstract AbstractChannel getPlayerChannel(R p);
	
	public void queuePacket(Player p, NPacket packet) {
		sendPacket(p, packet);
	}
	
	public void sendPacket(Player p, NPacket packet) {
		/*int packetId = version.getNamedVersion().getPacketId(packet.getPacketType());
		PacketSerializer serializer = new PacketSerializer(p);
		serializer.writeVarInt(packetId);
		packet.write(serializer, version);

		PacketSerializer send = new PacketSerializer(p);
		send.writeVarInt(serializer.writerIndex());
		send.writeVarInt(packetId);
		packet.write(send, version);
		getPlayerChannel(getR(p)).write(send);
		serializer.release();*/
	}

	protected <T> T get(Class<?> clazz, Object obj, String name) {
		try {
			Field f = clazz.getDeclaredField(name);
			f.setAccessible(true);
			return (T) f.get(obj);
		} catch (NoSuchFieldException e) { // prevent issue when wrong version
			Adapter.getAdapter().debug(Debug.GENERAL, "Failed to find field " + name + " in class " + obj.getClass().getSimpleName());
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
			Adapter.getAdapter().debug(Debug.GENERAL, "Failed to find method " + methodName + " in class " + obj.getClass().getSimpleName());
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
