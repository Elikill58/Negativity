package com.elikill58.negativity.sponge.nms;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.NPacket;

public abstract class SpongeVersionAdapter extends VersionAdapter<Player> {
	
	@Override
	public NPacket getPacket(Player player, Object nms) {
		String packetClassName = nms.getClass().getName();
		String packetName = packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
		return super.getPacket(player, nms, packetName);
	}
	
	private static SpongeVersionAdapter instance = new Sponge_1_12_2();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
