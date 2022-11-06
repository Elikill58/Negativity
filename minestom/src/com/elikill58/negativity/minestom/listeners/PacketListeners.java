package com.elikill58.negativity.minestom.listeners;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.elikill58.negativity.api.packets.nms.channels.java.JavaDecoderHandler;
import com.elikill58.negativity.minestom.impl.entity.MinestomEntityManager;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;

public class PacketListeners {

	private ExecutorService channelExecutor = Executors.newSingleThreadExecutor();

	public ExecutorService getOrCreateChannelExecutor() {
		return channelExecutor;
	}

	public PacketListeners(EventNode<Event> e) {
		e.addListener(PlayerSpawnEvent.class, this::onLogin);
		e.addListener(PlayerDisconnectEvent.class, this::onQuit);
	}

	public void onLogin(PlayerSpawnEvent e) {
		if (!e.isFirstSpawn())
			return;
		Player p = e.getPlayer();
		PlayerConnection co = p.getPlayerConnection();
		if (co instanceof PlayerSocketConnection connection) {
			getOrCreateChannelExecutor().execute(() -> {
				try {
					SocketChannel socket = connection.getChannel();
					socket.configureBlocking(false);
					Selector readSelector = Selector.open();
					readSelector.select(new JavaDecoderHandler(MinestomEntityManager.getPlayer(p)), MinecraftServer.TICK_MS);

					socket.register(readSelector, SelectionKey.OP_READ);
					readSelector.wakeup();
				} catch (ClosedChannelException exc) { // ignore
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			});
		}
	}

	public void onQuit(PlayerDisconnectEvent e) {

	}
}
