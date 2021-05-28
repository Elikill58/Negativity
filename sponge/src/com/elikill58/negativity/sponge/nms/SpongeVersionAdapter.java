package com.elikill58.negativity.sponge.nms;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.function.Function;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;
import com.elikill58.negativity.sponge.SpongeNegativity;

import net.minecraft.network.Packet;

public abstract class SpongeVersionAdapter {
	
	protected HashMap<String, Function<Packet<?>, NPacketPlayOut>> packetsPlayOut = new HashMap<String, Function<Packet<?>, NPacketPlayOut>>();
	protected HashMap<String, Function<Packet<?>, NPacketPlayIn>> packetsPlayIn = new HashMap<String, Function<Packet<?>, NPacketPlayIn>>();
	protected final String version;
	
	public SpongeVersionAdapter(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	public NPacket getPacket(Packet<?> nms, String packetName) {
		if(packetName.startsWith("CPacket"))
			return packetsPlayIn.getOrDefault(packetName, (obj) -> new NPacketPlayInUnset()).apply(nms);
		if(packetName.startsWith("SPacket"))
			return packetsPlayOut.getOrDefault(packetName, (obj) -> new NPacketPlayOutUnset()).apply(nms);
		/*if(packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		if(packetName.startsWith(PacketType.STATUS_PREFIX))
			return new NPacketStatusUnset();*/
		SpongeNegativity.getInstance().getLogger().info("Unknow packet " + packetName + ".");
		return null;
	}
	
	private static SpongeVersionAdapter instance = new Sponge_1_12_R1();
	
	public static SpongeVersionAdapter getVersionAdapter() {
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
