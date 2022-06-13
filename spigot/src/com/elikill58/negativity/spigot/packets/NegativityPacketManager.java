package com.elikill58.negativity.spigot.packets;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.packet.SpigotPacketManager;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibPacketManager;

public class NegativityPacketManager {

	private SpigotPacketManager spigotPacketManager;
	
	public NegativityPacketManager(SpigotNegativity pl) {
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
	}
	
	public SpigotPacketManager getPacketManager() {
		return spigotPacketManager;
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
}
