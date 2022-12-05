package com.elikill58.negativity.spigot.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.api.packets.nms.channels.netty.NettyPacketListener;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;

import io.netty.channel.Channel;

public class PacketListeners extends NettyPacketListener implements Listener {

	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_player_negativity", KEY_HANDSHAKE = "packet_handshake_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_server_negativity";
	
	public PacketListeners() {
		super(SpigotVersionAdapter.getVersionAdapter().getFuturChannel());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		join(SpigotEntityManager.getPlayer(p));
	}
	
	@EventHandler
	public void onLeft(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		left(SpigotEntityManager.getPlayer(p));
	}

	@Override
	public Channel getChannel(com.elikill58.negativity.api.entity.Player p) {
		return SpigotVersionAdapter.getVersionAdapter().getChannel((Player) p.getDefault());
	}
}
