package com.elikill58.negativity.sponge8.nms;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketPlayIn;
import com.elikill58.negativity.api.packets.packet.NPacketPlayOut;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUnset;
import com.elikill58.negativity.api.packets.packet.playout.NPacketPlayOutUnset;

import net.minecraft.network.protocol.Packet;

public abstract class SpongeVersionAdapter {
	
	protected Map<String, Function<Packet<?>, NPacketPlayOut>> packetsPlayOut = new HashMap<>();
	protected Map<String, Function<Packet<?>, NPacketPlayIn>> packetsPlayIn = new HashMap<>();
	protected final String version;
	
	public SpongeVersionAdapter(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	public NPacket getPacket(Packet<?> nmsPacket) {
		String packetClassName = nmsPacket.getClass().getName();
		String packetName = packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
		// see https://www.spigotmc.org/posts/3183758/
		if(packetName.startsWith("Serverbound"))
			return packetsPlayIn.getOrDefault(packetName, (obj) -> new NPacketPlayInUnset(packetName)).apply(nmsPacket);
		if(packetName.startsWith("Clientbound"))
			return packetsPlayOut.getOrDefault(packetName, (obj) -> new NPacketPlayOutUnset()).apply(nmsPacket);
		/*if(packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		if(packetName.startsWith(PacketType.STATUS_PREFIX))
			return new NPacketStatusUnset();*/
		return null;
	}
	
	private static SpongeVersionAdapter instance = new Sponge_1_16_5();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
