package com.elikill58.negativity.spigot.packets;

import org.bukkit.plugin.Plugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;

public class ProtocollibSupport {

	public static void run(Plugin pl) {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(pl, ListenerPriority.LOWEST, PacketType.Play.Client.getInstance()) {
					public void onPacketSending(PacketEvent e) {}

					public void onPacketReceiving(PacketEvent e) {
						if (!SpigotNegativityPlayer.INJECTED.contains(e.getPlayer().getUniqueId())) {
							return;
						}
						SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer());
						np.ALL++;
						if (e.getPacketType().equals(PacketType.Play.Client.FLYING)) {
							np.FLYING++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.POSITION_LOOK)) {
							np.POSITION_LOOK++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.BLOCK_PLACE)) {
							np.BLOCK_PLACE++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.BLOCK_DIG)) {
							np.BLOCK_DIG++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.KEEP_ALIVE)) {
							np.KEEP_ALIVE++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.POSITION)) {
							np.POSITION++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.ARM_ANIMATION)) {
							np.ARM++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.USE_ENTITY)) {
							np.USE_ENTITY++;
						} else if (e.getPacketType().equals(PacketType.Play.Client.ENTITY_ACTION)) {
							np.ENTITY_ACTION++;
						}
						if (!e.getPacketType().equals(PacketType.Play.Client.KEEP_ALIVE)) {
							np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
							np.LAST_OTHER_KEEP_ALIVE = e.getPacketType().name();
						}
					}
				});
	}
}
