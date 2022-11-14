package com.elikill58.negativity.minestom.nms;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.nms.channels.java.JavaChannel;

import net.minestom.server.entity.Player;
import net.minestom.server.network.player.PlayerConnection;
import net.minestom.server.network.player.PlayerSocketConnection;

public class MinestomVersionAdapter extends VersionAdapter<Player> {
	
	public MinestomVersionAdapter() {
		super("1_19_2");
	}

	@Override
	public AbstractChannel getPlayerChannel(Player p) {
		PlayerConnection co = p.getPlayerConnection();
		return co instanceof PlayerSocketConnection pos ? new JavaChannel(pos.getChannel()) : null;
	}
	
	private static MinestomVersionAdapter instance = new MinestomVersionAdapter();
	
	public static MinestomVersionAdapter getVersionAdapter() {
		return instance;
	}
}
