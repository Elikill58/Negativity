package com.elikill58.negativity.sponge.packets;

import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.packets.packetgate.PacketGateManager;
import com.elikill58.negativity.universal.PacketType.Client;

public class NegativityPacketManager {

	private PacketManager packetManager;
	
	public NegativityPacketManager(SpongeNegativity pl) {

		try {
			Class.forName("eu.crushedpixel.sponge.packetgate.api.registry.PacketGate");
			SpongeNegativity.hasPacketGate = true;
			packetManager = new PacketGateManager();
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
		
		packetManager.addHandler(new PacketHandler() {
			
			@Override
			public void onSend(AbstractPacket packet) {}
			
			@Override
			public void onReceive(AbstractPacket packet) {
				Player p = packet.getPlayer();
				if (!NegativityPlayer.INJECTED.contains(p.getUniqueId()))
					return;
				manageReceive(packet);
			}
		});
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		np.PACKETS.put(packet.getPacketType(), np.PACKETS.getOrDefault(packet.getPacketType(), 0) + 1);
		if (packet.getPacketType() != Client.KEEP_ALIVE) {
			np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
			np.LAST_OTHER_KEEP_ALIVE = packet.getPacketName();
		}
	}
}
