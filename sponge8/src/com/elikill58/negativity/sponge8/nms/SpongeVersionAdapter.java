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
		String packetName = nmsPacket.getClass().getCanonicalName().replace('.', '$');
		// see https://www.spigotmc.org/posts/3183758/
		if(packetName.contains("Serverbound"))
			return packetsPlayIn.getOrDefault(getParsedName(packetName, "Serverbound"), (obj) -> new NPacketPlayInUnset(getParsedName(packetName, "Serverbound"))).apply(nmsPacket);
		if(packetName.contains("Clientbound"))
			return packetsPlayOut.getOrDefault(getParsedName(packetName, "Clientbound"), (obj) -> new NPacketPlayOutUnset()).apply(nmsPacket);
		/*if(packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		if(packetName.startsWith(PacketType.STATUS_PREFIX))
			return new NPacketStatusUnset();*/
		return null;
	}
	
	private String getParsedName(String name, String key) {
		return key + name.split(key)[1];
	}
	
	private static SpongeVersionAdapter instance = new Sponge_1_16_5();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
