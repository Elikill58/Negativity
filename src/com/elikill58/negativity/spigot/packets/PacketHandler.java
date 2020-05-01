package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibPacketManager;

public abstract class PacketHandler {

	public abstract void onReceive(AbstractPacket packet);
	public abstract void onSend(AbstractPacket packet);
	
	// to don't recreate the same packetManager more than one time
	private static IPacketManager packetManager = null;
	
	public static IPacketManager run(Plugin pl) {
		if(packetManager != null)
			return packetManager;
		Plugin localPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
	    if (localPlugin != null) {
	    	pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
	    	return (packetManager = new ProtocollibPacketManager(pl));
	    } else
	    	return (packetManager = new CustomPacketManager(pl));
	}
}
