package com.elikill58.negativity.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import com.elikill58.negativity.api.packets.nms.channels.netty.NettyPacketListener;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.utils.SemVer;

import io.netty.channel.Channel;

public class PacketListeners extends NettyPacketListener implements Listener {

	
	public PacketListeners() {
		Bukkit.getOnlinePlayers().stream().map(SpigotEntityManager::getPlayer).forEach(this::join);
		Plugin pl = Bukkit.getPluginManager().getPlugin("ProtocolLib");
		if(pl != null && pl.isEnabled()) {
			if(SemVer.parse(pl.getDescription().getVersion()).isNewerThan(SemVer.parse("4.8.0")))
				return; // don't do this for newer version
			NettyPacketListener.DECODER_KEY = "protocol_lib_decoder";
			NettyPacketListener.ENCODER_KEY = "protocol_lib_encoder";
		}
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
