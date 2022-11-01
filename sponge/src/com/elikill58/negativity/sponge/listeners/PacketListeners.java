package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyPacketListener;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.nms.SpongeVersionAdapter;

import io.netty.channel.Channel;

public class PacketListeners extends NettyPacketListener {
	
	@Listener
	public void onPlayerJoin(ServerSideConnectionEvent.Join e, @First ServerPlayer p) {
		join(SpongeEntityManager.getPlayer(p));
	}
	
	@Listener
	public void onLeave(ServerSideConnectionEvent.Disconnect e, @First ServerPlayer p) {
		left(SpongeEntityManager.getPlayer(p));
	}

	@Override
	public Channel getChannel(Player p) {
		return SpongeVersionAdapter.getVersionAdapter().getChannel((ServerPlayer) p.getDefault());
	}
}
