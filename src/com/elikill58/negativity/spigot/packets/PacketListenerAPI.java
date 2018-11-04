package com.elikill58.negativity.spigot.packets;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.packets.ChannelInjector.ChannelWrapper;
import com.elikill58.negativity.spigot.packets.PacketAbstract.IPacketListener;

public class PacketListenerAPI implements IPacketListener, Listener {

	private static ChannelInjector channelInjector;
	protected boolean injected = false;

	public PacketListenerAPI(Plugin pl) {
		channelInjector = new ChannelInjector();
		injected = channelInjector.inject(this);
		pl.getServer().getPluginManager().registerEvents(this, pl);
	}

	public static boolean addPacketHandler(PacketHandler handler) {
		return PacketHandler.addHandler(handler);
	}

	public static boolean removePacketHandler(PacketHandler handler) {
		return PacketHandler.removeHandler(handler);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		channelInjector.addChannel(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		channelInjector.removeChannel(e.getPlayer());
	}

	public static void addPlayer(Player p) {
		if(!PacketManager.hasProtocollib && !channelInjector.contains(p))
			channelInjector.addChannel(p);
	}

	public static void removePlayer(Player p) {
		if(!PacketManager.hasProtocollib && channelInjector.contains(p))
			channelInjector.removeChannel(p);
	}

	@Override
	public Object onPacketReceive(Object sender, Object packet) {
		ReceivedPacket receivedPacket;
		if (sender instanceof Player)
			receivedPacket = new ReceivedPacket(packet, (Player) sender);
		else
			receivedPacket = new ReceivedPacket(packet, (ChannelWrapper<?>) sender);
		PacketHandler.notifyHandlers(receivedPacket);
		if (receivedPacket.getPacket() != null)
			return receivedPacket.getPacket();
		return packet;
	}
}
