package com.elikill58.negativity.sponge7.packets.packetgate;

import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.sponge7.impl.packet.SpongePacketManager;
import com.elikill58.negativity.sponge7.nms.SpongeVersionAdapter;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListener.ListenerPriority;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketGate;
import net.minecraft.network.Packet;

public class PacketGateManager extends SpongePacketManager {

	private final PacketGate packetGate;
	
	public PacketGateManager() {
		packetGate = Sponge.getServiceManager().provideUnchecked(PacketGate.class);
		packetGate.registerListener(new PacketGateListener(this), ListenerPriority.DEFAULT);
	}
	
	public AbstractPacket onPacketSent(NPacket packet, Player sender, Object nmsPacket, PacketEvent event) {
		PacketGatePacket customPacket = new PacketGatePacket(packet, nmsPacket, sender, event);
		notifyHandlersSent(PacketSourceType.PACKETGATE, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket packet, Player sender, Object nmsPacket, PacketEvent event) {
		PacketGatePacket customPacket = new PacketGatePacket(packet, nmsPacket, sender, event);
		notifyHandlersReceive(PacketSourceType.PACKETGATE, customPacket);
		return customPacket;
	}

	public class PacketGateListener extends PacketListenerAdapter {

		private final PacketGateManager packetManager;

		public PacketGateListener(PacketGateManager packetManager) {
			this.packetManager = packetManager;
		}

		@Override
		public void onPacketRead(PacketEvent e, PacketConnection connection) {
			UUID uuid = connection.getPlayerUUID();
			if(uuid == null)
				return;
			Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(uuid);
			if (!optionalPlayer.isPresent())
				return;
			Player p = optionalPlayer.get();
			/*PacketType packetType = null;
			if (packetName.equalsIgnoreCase("CPacketPlayer"))
				packetType = PacketType.Client.FLYING;
			else {
				String newName = packetName.replaceFirst("CPacket", (e.isOutgoing() ? "PacketPlayOut" : "PacketPlayIn"))
						.replaceAll("\\$", "").replaceAll("Player", "");
				packetType = PacketType.getType(newName);
				if (packetType == null)
					SpongeNegativity.getInstance().getLogger().error("Unknow Packet " + packetName + ", parsed as "
							+ newName + ". Please, report this to Elikill58.");
			}*/
			SpongeVersionAdapter ada = SpongeVersionAdapter.getVersionAdapter();
			Packet<?> nmsPacket = e.getPacket();
			AbstractPacket packet = packetManager.onPacketReceive(ada.getPacket(p, PacketDirection.CLIENT_TO_SERVER, nmsPacket), p, nmsPacket, e);
			e.setCancelled(packet.isCancelled());
		}
		
		@Override
		public void onPacketWrite(PacketEvent e, PacketConnection connection) {
			UUID uuid = connection.getPlayerUUID();
			if(uuid == null)
				return;
			Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(uuid);
			if (!optionalPlayer.isPresent())
				return;
			Player p = optionalPlayer.get();
			SpongeVersionAdapter ada = SpongeVersionAdapter.getVersionAdapter();
			Packet<?> nmsPacket = e.getPacket();
			AbstractPacket packet = packetManager.onPacketSent(ada.getPacket(p, PacketDirection.SERVER_TO_CLIENT, nmsPacket), p, nmsPacket, e);
			e.setCancelled(packet.isCancelled());
		}
	}
}
