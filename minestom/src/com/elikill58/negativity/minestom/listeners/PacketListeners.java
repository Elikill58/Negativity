package com.elikill58.negativity.minestom.listeners;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.packets.nms.channels.java.JavaDecoderHandler;
import com.elikill58.negativity.minestom.impl.entity.MinestomPlayer;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;

public class PacketListeners {

	public PacketListeners(EventNode<Event> e) {
		e.addListener(PlayerSpawnEvent.class, this::onLogin);
	}

	public void onLogin(PlayerSpawnEvent e) {
		if (!e.isFirstSpawn())
			return;
		Player p = e.getPlayer();
		PlayerConnection co = p.getPlayerConnection();
		if (co instanceof PlayerSocketConnection connection) {
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p.getUuid(), () -> new MinestomPlayer(p));
			try {
				SocketChannel socket = connection.getChannel();
				socket.configureBlocking(false);
				Selector readSelector = Selector.open();
				readSelector.select(new JavaDecoderHandler(np.getPlayer()), MinecraftServer.TICK_MS);

				socket.register(readSelector, SelectionKey.OP_READ);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		}

	}
}
