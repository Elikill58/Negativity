package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketHandler;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.packet.SpigotPacketManager;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibPacketManager;
import com.elikill58.negativity.universal.PacketType;

public class NegativityPacketManager {

	private SpigotPacketManager spigotPacketManager;
	private SpigotNegativity plugin;
	
	public NegativityPacketManager(SpigotNegativity pl) {
		this.plugin = pl;
		Plugin protocolLibPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (protocolLibPlugin != null) {
			if(checkProtocollibConditions()) {
				pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
				spigotPacketManager = new ProtocollibPacketManager(pl);
			} else {
				pl.getLogger().warning("The plugin ProtocolLib has been detected but you have an OLD version, so we cannot use it.");
				pl.getLogger().warning("Fallback to default Packet system ...");
				spigotPacketManager = new CustomPacketManager(pl);
			}
		} else
			spigotPacketManager = new CustomPacketManager(pl);
		spigotPacketManager.addHandler(new PacketHandler() {
			
			@Override
			public void onSend(AbstractPacket packet) {}
			
			@Override
			public void onReceive(AbstractPacket packet) {
				Player p = packet.getPlayer();
				if (!NegativityPlayer.INJECTED.contains(p.getUniqueId()))
					return;
				if(!plugin.isEnabled())
					return;
				manageReceive(packet);
			}
		});
	}
	
	public SpigotPacketManager getPacketManager() {
		return spigotPacketManager;
	}
	
	private boolean checkProtocollibConditions() {
		try {
			Class.forName("com.comphenix.protocol.injector.server.TemporaryPlayer");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
	
	private void manageReceive(AbstractPacket packet) {
		Player p = packet.getPlayer();
		NegativityPlayer np = NegativityPlayer.getCached(p.getUniqueId());
		np.ALL_PACKETS++;
		PacketType type = packet.getPacketType();
		np.PACKETS.put(type, np.PACKETS.getOrDefault(type, 0) + 1);
		if(type == PacketType.Client.USE_ENTITY) {
			try {
				/*int id = packet.getContent().getIntegers().read(0);
				for(FakePlayer fp : np.getFakePlayers())
					if(fp.getEntityId() == id)
						np.removeFakePlayer(fp, true);*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
