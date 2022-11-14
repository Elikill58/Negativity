package com.elikill58.negativity.sponge7.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.elikill58.negativity.api.packets.nms.channels.netty.NettyPacketListener;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge7.nms.SpongeVersionAdapter;

import io.netty.channel.Channel;

public class PacketListeners extends NettyPacketListener {
	
	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join e, @First Player p) {
		join(SpongeEntityManager.getPlayer(p));
	}
	
	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e, @First Player p) {
		left(SpongeEntityManager.getPlayer(p));
	}

	@Override
	public Channel getChannel(com.elikill58.negativity.api.entity.Player p) {
		return SpongeVersionAdapter.getVersionAdapter().getChannel((Player) p.getDefault());
	}
}
