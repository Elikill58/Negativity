package com.elikill58.negativity.spigot.packets.custom.channel;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;

public abstract class ChannelAbstract {

	private ExecutorService addChannelExecutor, removeChannelExecutor;
	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_player_negativity", KEY_HANDSHAKE = "packet_handshake_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_server_negativity";

	private CustomPacketManager customPacketManager;
	private HashSet<UUID> players = new HashSet<>();
	
	protected ChannelAbstract(CustomPacketManager customPacketManager) {
		this.customPacketManager = customPacketManager;
	}
	
	public CustomPacketManager getPacketManager() {
		return customPacketManager;
	}
	
	public ExecutorService getAddChannelExecutor() {
		return addChannelExecutor;
	}
	
	public ExecutorService getOrCreateAddChannelExecutor() {
		if(addChannelExecutor == null)
			addChannelExecutor = Executors.newSingleThreadExecutor();
		return addChannelExecutor;
	}
	
	public ExecutorService getRemoveChannelExecutor() {
		return removeChannelExecutor;
	}
	
	public ExecutorService getOrCreateRemoveChannelExecutor() {
		if(removeChannelExecutor == null)
			removeChannelExecutor = Executors.newSingleThreadExecutor();
		return removeChannelExecutor;
	}

	public void addPlayer(Player p) {
		if(players.add(p.getUniqueId())) {
			addChannel(p, p.getUniqueId().toString());
			try {
				Integer protocol = customPacketManager.protocolVersionPerChannel.remove(getChannel(p));
				if(protocol != null)
					SpigotEntityManager.getPlayer(p).setProtocolVersion(protocol);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void removePlayer(Player p) {
		if(players.remove(p.getUniqueId()))
			removeChannel(p, p.getUniqueId().toString());
	}
	
	public abstract void addChannel(Player player, String endChannelName);

	public abstract void removeChannel(Player player, String endChannelName);
	
	public abstract Object getChannel(Player p) throws Exception;

}
