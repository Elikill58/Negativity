package com.elikill58.negativity.spigot.packets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.packets.protocollib.ProtocollibPacketManager;

public abstract class PacketHandler {

	public abstract void onReceive(AbstractPacket packet);

	public abstract void onSend(AbstractPacket packet);

	// to don't recreate the same packetManager more than one time
	private static PacketManager packetManager = null;

	public static PacketManager run(Plugin pl) {
		if (packetManager != null)
			return packetManager;
		Plugin protocolLibPlugin = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if (protocolLibPlugin != null) {
			pl.getLogger().info("The plugin ProtocolLib has been detected. Loading Protocollib support ...");
			packetManager = new ProtocollibPacketManager(pl);
		} else
			packetManager = new CustomPacketManager(pl);
		return packetManager;
	}
}
