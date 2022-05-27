package com.elikill58.negativity.sponge9.packets;

import org.apache.logging.log4j.Logger;

import com.elikill58.negativity.sponge9.SpongeNegativity;
import com.elikill58.negativity.sponge9.impl.packet.SpongePacketManager;
import com.elikill58.negativity.sponge9.packets.packetgate.PacketGateManager;

public class NegativityPacketManager {

	private SpongePacketManager spongePacketManager;

	public NegativityPacketManager(SpongeNegativity pl) {
		try {
			Class.forName("eu.crushedpixel.sponge.packetgate.api.registry.PacketGate");
			spongePacketManager = new PacketGateManager();
			pl.getLogger().info("Loaded PacketGate support");
		} catch (ClassNotFoundException e1) {
			Logger log = pl.getLogger();
			log.warn("----- Negativity Problem -----");
			log.warn("");
			log.warn("Error while loading PacketGate. Plugin not found.");
			log.warn("Please download it available here: https://github.com/Elikill58/PacketGate/releases");
			log.warn("Then, put it in the mods folder.");
			log.warn("Restart your server and now, it will be working");
			log.warn("");
			log.warn("----- Negativity Problem -----");
		}
	}

	public SpongePacketManager getSpongePacketManager() {
		return spongePacketManager;
	}
}
