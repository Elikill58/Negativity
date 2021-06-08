package com.elikill58.negativity.spigot.nms;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketUnknown;
import com.elikill58.negativity.spigot.SpigotNegativity;

public class Spigot_UnknowVersion extends SpigotVersionAdapter {
	
	public Spigot_UnknowVersion(String version) {
		super(version);
		SpigotNegativity.getInstance().getLogger().warning("Failed to find version adapter for " + version + ".");
	}
	
	@Override
	protected String isOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public NPacket getPacket(Object nms, String packetName) {
		return new NPacketUnknown();
	}
	
}
