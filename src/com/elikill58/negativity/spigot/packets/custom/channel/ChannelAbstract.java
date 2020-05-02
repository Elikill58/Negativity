package com.elikill58.negativity.spigot.packets.custom.channel;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;

public abstract class ChannelAbstract {

	private ExecutorService addChannelExecutor, removeChannelExecutor;
	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_listener_player_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_listener_server_negativity";

	private CustomPacketManager customPacketManager;
	private int i = 0;
	private HashMap<UUID, String> players = new HashMap<>();
	
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
		if(players.containsKey(p.getUniqueId()))
			return;
		String channelName = getPlayerNewChannelName(p);
		players.put(p.getUniqueId(), channelName);
		addChannel(p, channelName);
	}

	public void removePlayer(Player p) {
		String channelName = players.remove(p.getUniqueId());
		if(channelName != null)
			removeChannel(p, channelName);
	}
	
	public abstract void addChannel(Player player, String endChannelName);

	public abstract void removeChannel(Player player, String endChannelName);

	private String getPlayerNewChannelName(Player player) {
		while(players.containsValue("-" + i))
			i++;
		return "-" + i;
	}
}
