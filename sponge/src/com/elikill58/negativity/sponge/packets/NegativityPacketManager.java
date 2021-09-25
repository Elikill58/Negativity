package com.elikill58.negativity.sponge.packets;

import org.slf4j.Logger;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerDamageEntityEvent;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketHandler;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInUseEntity;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.packet.SpongePacketManager;
import com.elikill58.negativity.sponge.packets.packetgate.PacketGateManager;

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
		
		spongePacketManager.addHandler(new PacketHandler() {
			
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

		PacketType type = packet.getPacketType();
		if(type == PacketType.Client.USE_ENTITY) {
			NPacketPlayInUseEntity useEntityPacket = (NPacketPlayInUseEntity) packet.getPacket();
			for(Entity entity : p.getWorld().getEntities()) {
				if(entity.getEntityId() == useEntityPacket.entityId) {
					PlayerDamageEntityEvent event = new PlayerDamageEntityEvent(p, entity);
					EventManager.callEvent(event);
					if(event.isCancelled())
						packet.setCancelled(event.isCancelled());
				}
			}
		}
	}
}
