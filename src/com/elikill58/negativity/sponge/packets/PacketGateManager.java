package com.elikill58.negativity.sponge.packets;

import org.spongepowered.api.Sponge;

import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;

public class PacketGateManager {

	public static void check() {
		new Thread(() -> {
			Sponge.getServiceManager().provide(PacketGate.class).ifPresent((packetGate) -> {
				packetGate.registerListener(new PacketManager(), ListenerPriority.DEFAULT);
			});
		}).run();
	}

}
