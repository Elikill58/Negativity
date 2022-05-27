package com.elikill58.negativity.sponge9.nms;

import java.util.Arrays;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;

public abstract class SpongeVersionAdapter extends VersionAdapter<ServerPlayer> {
	
	public SpongeVersionAdapter(String version) {
		super(version);
	}
	
	@Override
	public String getNameOfPacket(Object nms) {
		String formattedPacketName = nms.getClass().getCanonicalName().replace('.', '$');
		// see https://www.spigotmc.org/posts/3183758/
		for(String possibleType : Arrays.asList("Serverbound", "Clientbound")) {
			if(formattedPacketName.contains(possibleType))
				formattedPacketName = getParsedName(formattedPacketName, possibleType);
		}
		String[] splittedFormatted = formattedPacketName.split("\\.");
		return splittedFormatted[splittedFormatted.length - 1];
	}
	
	/*public NPacket getPacket(Packet<?> nmsPacket) {
		String packetName = nmsPacket.getClass().getCanonicalName().replace('.', '$');
		if(packetName.contains("Serverbound"))
			return packetsPlayIn.getOrDefault(getParsedName(packetName, "Serverbound"), (obj) -> new NPacketPlayInUnset(getParsedName(packetName, "Serverbound"))).apply(nmsPacket);
		if(packetName.contains("Clientbound"))
			return packetsPlayOut.getOrDefault(getParsedName(packetName, "Clientbound"), (obj) -> new NPacketPlayOutUnset()).apply(nmsPacket);
		return null;
	}*/
	
	public String getParsedName(String name, String key) {
		return key + name.split(key)[1];
	}
	
	private static SpongeVersionAdapter instance = new Sponge_1_16_5();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
