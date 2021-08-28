package com.elikill58.negativity.spigot.nms;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketUnknown;
import com.elikill58.negativity.spigot.SpigotNegativity;

public class Spigot_UnknowVersion extends SpigotVersionAdapter {
	
	public Spigot_UnknowVersion(String version) {
		super(version);
		SpigotNegativity.getInstance().getLogger().warning("Failed to find version adapter for " + version + ".");
	}
	
	@Override
	protected String getOnGroundFieldName() {
		return "f";
	}
	
	@Override
	public double getAverageTps() {
		return 0;
	}
	
	@Override
	public NPacket getPacket(Player player, Object nms, String packetName) {
		return new NPacketUnknown();
	}
	
	@Override
	public int getPlayerPing(Player player) {
		return 0;
	}
}
