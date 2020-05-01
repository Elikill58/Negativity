package com.elikill58.orebfuscator.packets.custom;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.elikill58.orebfuscator.packets.AbstractPacket;
import com.elikill58.orebfuscator.packets.IPacketManager;
import com.elikill58.orebfuscator.packets.PacketType.AbstractPacketType;
import com.elikill58.orebfuscator.packets.custom.channel.ChannelInjector;
import com.elikill58.orebfuscator.packets.event.PacketEvent.PacketSourceType;
import com.elikill58.orebfuscator.utils.Utils;

public class CustomPacketManager implements IPacketManager, Listener {
	
	private ChannelInjector channelInjector;
	private Plugin pl;

	public CustomPacketManager(Plugin pl) {
		this.pl = pl;
		(channelInjector = new ChannelInjector(this)).inject();
		pl.getServer().getPluginManager().registerEvents(this, pl);
		
		for(Player p : Utils.getOnlinePlayers())
			addPlayer(p);
	}
	
	public Plugin getPlugin() {
		return pl;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		addPlayer(e.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		removePlayer(e.getPlayer());
	}

	@Override
	public void addPlayer(Player p) {
		if(!channelInjector.contains(p))
			channelInjector.addChannel(p);
	}

	@Override
	public void removePlayer(Player p) {
		if(channelInjector.contains(p))
			channelInjector.removeChannel(p);
	}

	@Override
	public void clear() {
		for(Player temp : channelInjector.getPlayers())
			removePlayer(temp);
	}

	public AbstractPacket onPacketSent(AbstractPacketType type, Player sender, Object packet) {
		CustomPacket customPacket = new CustomPacket(type, packet, sender);
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(AbstractPacketType type, Player sender, Object packet) {
		CustomPacket customPacket = new CustomPacket(type, packet, sender);
		notifyHandlersReceive(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}
}
