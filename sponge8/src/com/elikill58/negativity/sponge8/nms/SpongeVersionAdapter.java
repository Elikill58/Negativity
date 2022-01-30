package com.elikill58.negativity.sponge8.nms;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.universal.Adapter;

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
		if(packetName.contains("Serverbound"))
			return packetsPlayIn.bukkitToNegativity(player, nms);
		else if(packetName.contains("Clientbound"))
			return packetsPlayOut.bukkitToNegativity(player, nms);
		else if (packetName.startsWith(PacketType.LOGIN_PREFIX))
			return new NPacketLoginUnset();
		else if (packetName.startsWith(PacketType.STATUS_PREFIX))
			return packetsStatus.bukkitToNegativity(player, nms);
		else if (packetName.startsWith(PacketType.HANDSHAKE_PREFIX))
			return packetsHandshake.bukkitToNegativity(player, nms);
		Adapter.getAdapter().debug("[SpigotVersionAdapter] Unknow packet " + packetName + ".");
		return null;
	}
	
	/*public NPacket getPacket(Packet<?> nmsPacket) {
		String packetName = nmsPacket.getClass().getCanonicalName().replace('.', '$');
		// see https://www.spigotmc.org/posts/3183758/
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
