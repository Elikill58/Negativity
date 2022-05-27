package com.elikill58.negativity.sponge7.nms;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;

public abstract class SpongeVersionAdapter extends VersionAdapter<Player> {
	
	public SpongeVersionAdapter(String version) {
		super(version);
	}
	
	@Override
	public String getNameOfPacket(Object nms) {
		String packetClassName = nms.getClass().getName();
		return packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
	}
	
	private static SpongeVersionAdapter instance = new Sponge_1_12_2();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
