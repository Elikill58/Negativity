package com.elikill58.negativity.spigot.packets;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.FakePlayer;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibPacketManager;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.PacketType.Client;
import com.elikill58.negativity.universal.adapter.Adapter;

public class NegativityPacketManager {

	private PacketManager packetManager;
	private SpigotNegativity plugin;
	
	public NegativityPacketManager(SpigotNegativity pl) {
		this.plugin = pl;
		Plugin protocolLibPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (protocolLibPlugin != null) {
			if(checkProtocollibConditions()) {
				pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
				packetManager = new ProtocollibPacketManager(pl);
			} else {
				pl.getLogger().warning("The plugin ProtocolLib has been detected but you have an old or too new version, so we cannot use it.");
				pl.getLogger().warning("Fallback to default Packet system ...");
				packetManager = new CustomPacketManager(pl);
			}
		} else
			packetManager = new CustomPacketManager(pl);
		packetManager.addHandler(new PacketHandler() {
			
			@Override
			public void onSend(AbstractPacket packet) {}
			
			@Override
			public void onReceive(AbstractPacket packet) {
				if(!packet.hasPlayer()) {
					Adapter.getAdapter().debug("Packet: " + packet.getPacketType().getFullName() +  " : " + packet.getPacketType().getPacketName());
					return;
				}
				Player p = packet.getPlayer();
				if (!SpigotNegativityPlayer.INJECTED.contains(p.getUniqueId()))
					return;
				if(!plugin.isEnabled())
					return;
				manageReceive(packet);
			}
		});
	}
	
	public PacketManager getPacketManager() {
		return packetManager;
	}
	
	private boolean checkProtocollibConditions() {
		for(String searchedClass : Arrays.asList("com.comphenix.protocol.injector.server.TemporaryPlayer", "com.comphenix.protocol.injector.temporary.TemporaryPlayer")) { // class since 4.4.0 until 4.8.0, then the new one
			try {
				Class.forName(searchedClass);
				return true; // class found
			} catch (ClassNotFoundException e) {}
		}
		return false;
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		np.ALL++;
		PacketType type = packet.getPacketType();
		np.PACKETS.put(type, np.PACKETS.getOrDefault(type, 0) + 1);
		if(type == PacketType.Client.USE_ENTITY) {
			try {
				int id = packet.getContent().getIntegers().read(0);
				for(FakePlayer fp : np.getFakePlayers())
					if(fp.getEntityId() == id)
						np.removeFakePlayer(fp, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(type == PacketType.Client.POSITION || type == PacketType.Client.FLYING || type == PacketType.Client.LOOK || type == PacketType.Client.POSITION_LOOK) {
			np.setOnGround(packet.getContent().getBooleans().read(0));
		}
		if (packet.getPacketType() != Client.KEEP_ALIVE) {
			np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
			np.LAST_OTHER_KEEP_ALIVE = packet.getPacketName();
		}
	}
}
