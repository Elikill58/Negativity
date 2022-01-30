package com.elikill58.negativity.sponge8.nms;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.NPacket;

public abstract class SpongeVersionAdapter extends VersionAdapter<ServerPlayer> {
	
	protected final String version;
	
	public SpongeVersionAdapter(String version) {
		this.version = version;
	}
	
	public String getVersion() {
		return version;
	}
	
	@Override
	public NPacket getPacket(ServerPlayer player, Object nms) {
		String packetName = nms.getClass().getCanonicalName().replace('.', '$');
		// see https://www.spigotmc.org/posts/3183758/
		if(packetName.contains("Serverbound"))
			return getPacket(player, nms, getParsedName(packetName, "Serverbound"));
		else if(packetName.contains("Clientbound"))
			return getPacket(player, nms, getParsedName(packetName, "Clientbound"));
		return getPacket(player, nms, packetName);
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
