package com.elikill58.orebfuscator.packets.custom.channel;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bukkit.entity.Player;

import com.elikill58.orebfuscator.packets.custom.CustomPacketManager;

public abstract class ChannelAbstract {

	final Executor addChannelExecutor = Executors.newSingleThreadExecutor(), removeChannelExecutor = Executors.newSingleThreadExecutor();
	static final String KEY_HANDLER = "packet_handler", KEY_PLAYER = "packet_listener_player_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_listener_server_negativity";

	private CustomPacketManager customPacketManager;
	
	protected ChannelAbstract(CustomPacketManager customPacketManager) {
		this.customPacketManager = customPacketManager;
	}
	
	public CustomPacketManager getPacketManager() {
		return customPacketManager;
	}
	
	public abstract void addChannel(Player player);

	public abstract void removeChannel(Player player);
}
