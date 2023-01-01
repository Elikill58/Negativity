package com.elikill58.negativity.sponge.packets;

import org.slf4j.Logger;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.packets.packetgate.PacketGateManager;
import com.elikill58.negativity.universal.PacketType;
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
				if (!SpongeNegativityPlayer.INJECTED.contains(p))
					return;
				manageReceive(packet);
			}
		});
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
		np.ALL++;
		if(packet.getPacketType() instanceof PacketType.Client) {
			switch (((Client) packet.getPacketType())) {
			case FLYING:
				np.FLYING++;
				break;
			case KEEP_ALIVE:
				np.KEEP_ALIVE++;
				break;
			case POSITION_LOOK:
				np.POSITION_LOOK++;
				break;
			case BLOCK_PLACE:
				np.BLOCK_PLACE++;
				break;
			case BLOCK_DIG:
				np.BLOCK_DIG++;
				break;
			case POSITION:
				np.POSITION++;
				break;
			case ARM_ANIMATION:
				np.ARM++;
				break;
			case USE_ENTITY:
				np.USE_ENTITY++;
				break;
			case ENTITY_ACTION:
				np.ENTITY_ACTION++;
				break;
			default:
			}
		}
	}
}
