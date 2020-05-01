package com.elikill58.orebfuscator.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.orebfuscator.packets.custom.CustomPacketManager;
import com.elikill58.orebfuscator.packets.protocollib.ProtocollibPacketManager;

public abstract class PacketHandler {

	public abstract void onReceive(AbstractPacket packet);
	public abstract void onSend(AbstractPacket packet);

	public static IPacketManager run(Plugin pl) {
		Plugin localPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
	    if (localPlugin != null) {
	    	pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
	    	return new ProtocollibPacketManager(pl);
	    } else
	    	return new CustomPacketManager(pl);
	}
}
