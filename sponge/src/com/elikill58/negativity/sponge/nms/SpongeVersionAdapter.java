package com.elikill58.negativity.sponge.nms;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.login.NPacketLoginUnset;
import com.elikill58.negativity.universal.Adapter;

public abstract class SpongeVersionAdapter extends VersionAdapter<Player> {
	
	@Override
	public NPacket getPacket(Player player, Object nms) {
		String packetClassName = nms.getClass().getName();
		String packetName = packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
		if (packetName.startsWith("CPacket"))
			return packetsPlayIn.bukkitToNegativity(player, nms);
		else if (packetName.startsWith("SPacket"))
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
	
	private static SpongeVersionAdapter instance = new Sponge_1_12_2();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
