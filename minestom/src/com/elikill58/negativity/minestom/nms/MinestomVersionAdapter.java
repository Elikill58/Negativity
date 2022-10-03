package com.elikill58.negativity.minestom.nms;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;

import net.minestom.server.entity.Player;

public abstract class MinestomVersionAdapter extends VersionAdapter<Player> {
	
	public MinestomVersionAdapter(String version) {
		super(version);
	}
	
	@Override
	public String getNameOfPacket(Object nms) {
		String packetClassName = nms.getClass().getName();
		return packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
	}
	
	private static MinestomVersionAdapter instance = new Minestom_1_18_2();
	
	public static MinestomVersionAdapter getVersionAdapter() {
		return instance;
	}
}
