package com.elikill58.negativity.sponge7.packets;

import org.slf4j.Logger;

import com.elikill58.negativity.sponge7.SpongeNegativity;
import com.elikill58.negativity.sponge7.impl.packet.SpongePacketManager;
import com.elikill58.negativity.sponge7.packets.packetgate.PacketGateManager;

public class NegativityPacketManager {

	private SpongePacketManager spongePacketManager;

	public NegativityPacketManager(SpongeNegativity pl) {

		try {
			Class.forName("eu.crushedpixel.sponge.packetgate.api.registry.PacketGate");
			SpongeNegativity.hasPacketGate = true;
			spongePacketManager = new PacketGateManager();
		} catch (ClassNotFoundException e1) {
			Logger log = pl.getLogger();
			log.warn("----- Negativity Problem -----");
			log.warn("");
			log.warn("Error while loading PacketGate. Plugin not found.");
			log.warn("Please download it available here: https://github.com/CrushedPixel/PacketGate/releases");
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
