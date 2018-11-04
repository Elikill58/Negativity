package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class PacketManager {
	
	public static boolean hasProtocollib = false;
	
	public static void run(Plugin pl) {
		Plugin localPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
	    if (localPlugin != null)
	    	hasProtocollib = true;
	    if(hasProtocollib)
	    	ProtocollibSupport.run(pl);
	    else
	    	PacketHandler.run(pl);
	}
}
