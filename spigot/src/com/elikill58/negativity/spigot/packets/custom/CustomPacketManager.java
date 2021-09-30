package com.elikill58.negativity.spigot.packets.custom;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.impl.packet.SpigotPacketManager;
import com.elikill58.negativity.spigot.packets.custom.channel.ChannelAbstract;
import com.elikill58.negativity.spigot.packets.custom.channel.INCChannel;
import com.elikill58.negativity.spigot.packets.custom.channel.NMUChannel;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

public class CustomPacketManager extends SpigotPacketManager implements Listener {
	
	private ChannelAbstract channel;
	private Plugin pl;
	public HashMap<Object, Integer> protocolVersionPerChannel = new HashMap<>();
	private boolean isStarted = false;

	public CustomPacketManager(Plugin pl) {
		this.pl = pl;
		if (Version.getVersion(Utils.VERSION).equals(Version.V1_7))
			channel = new NMUChannel(this);
		else
			channel = new INCChannel(this);
	}
	
	public Plugin getPlugin() {
		return pl;
	}
	
	@Override
	public void load() {
		// we wait the start server
		pl.getServer().getPluginManager().registerEvents(this, pl);
		Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
			@Override
			public void run() {
				isStarted = true;
				for(Player p : Adapter.getAdapter().getOnlinePlayers())
					addPlayer(p);
			}
		}, 40);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		addPlayer(SpigotEntityManager.getPlayer(e.getPlayer()));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		removePlayer(SpigotEntityManager.getPlayer(e.getPlayer()));
	}

	@Override
	public void addPlayer(Player p) {
		if(isStarted)
			channel.addPlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@Override
	public void removePlayer(Player p) {
		channel.removePlayer((org.bukkit.entity.Player) p.getDefault());
	}

	@Override
	public void clear() {
		for(Player player : Adapter.getAdapter().getOnlinePlayers())
			removePlayer(player);
		if(channel.getAddChannelExecutor() != null)
			channel.getAddChannelExecutor().shutdownNow();
		if(channel.getRemoveChannelExecutor() != null)
			channel.getRemoveChannelExecutor().shutdownNow();
	}

	public AbstractPacket onPacketSent(NPacket commonPacket, Player sender, Object packet) {
		CustomPacket customPacket = new CustomPacket(commonPacket, packet, sender);
		if (commonPacket == null) {
			return customPacket;
		}
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket commonPacket, Player sender, Object packet) {
		CustomPacket customPacket = new CustomPacket(commonPacket, packet, sender);
		if (commonPacket == null) {
			return customPacket;
		}
		notifyHandlersReceive(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}
}
