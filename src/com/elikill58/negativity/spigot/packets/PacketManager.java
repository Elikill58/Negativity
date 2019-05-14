package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibSupport;

public class PacketManager {
	
	public static boolean hasProtocollib = false;
	
	public static void run(Plugin pl) {
		Plugin localPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
	    if (localPlugin != null)
	    	hasProtocollib = true;
	    if(hasProtocollib) {
	    	pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
	    	ProtocollibSupport.run(pl);
	    } else
	    	PacketHandler.run(pl);
	}
}
