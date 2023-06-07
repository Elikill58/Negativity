package com.elikill58.negativity.spigot.packets.custom;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.PacketManager;
import com.elikill58.negativity.spigot.packets.custom.channel.ChannelAbstract;
import com.elikill58.negativity.spigot.packets.custom.channel.INC2Channel;
import com.elikill58.negativity.spigot.packets.custom.channel.INCChannel;
import com.elikill58.negativity.spigot.packets.event.PacketEvent.PacketSourceType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.Version;

public class CustomPacketManager extends PacketManager implements Listener {
	
	private ChannelAbstract channel;
	private Plugin pl;
	public HashMap<Object, Integer> protocolVersionPerChannel = new HashMap<>();
	private boolean isStarted = false;

	public CustomPacketManager(Plugin pl) {
		this.pl = pl;
		Version version = Version.getVersion(Utils.VERSION);
		if (version.isNewerOrEquals(Version.V1_17))
			channel = new INC2Channel(this);
		else
			channel = new INCChannel(this);
		pl.getServer().getPluginManager().registerEvents(this, pl);
		
		// we wait the start server
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				isStarted = true;
				for(Player p : Utils.getOnlinePlayers())
					addPlayer(p);
			}
		}, 40);
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
		if(isStarted)
			channel.addPlayer(p);
	}

	@Override
	public void removePlayer(Player p) {
		channel.removePlayer(p);
	}

	@Override
	public void clear() {
		for(Player player : Utils.getOnlinePlayers())
			removePlayer(player);
		if(channel.getAddChannelExecutor() != null)
			channel.getAddChannelExecutor().shutdownNow();
		if(channel.getRemoveChannelExecutor() != null)
			channel.getRemoveChannelExecutor().shutdownNow();
	}

	public AbstractPacket onPacketSent(PacketType type, Player sender, Object packet) {
		if(type == null)
			return null;
		CustomPacket customPacket = new CustomPacket(type, packet, sender);
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(PacketType type, Player sender, Object packet) {
		if(type == null)
			return null;
		CustomPacket customPacket = new CustomPacket(type, packet, sender);
		notifyHandlersReceive(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}
}
