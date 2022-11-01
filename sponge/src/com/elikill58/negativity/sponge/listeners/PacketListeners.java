package com.elikill58.negativity.sponge.listeners;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ServerSideConnectionEvent;

import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyDecoderHandler;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyEncoderHandler;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge.nms.SpongeVersionAdapter;

import io.netty.channel.Channel;

public class PacketListeners {

	private ExecutorService addChannelExecutor = Executors.newSingleThreadExecutor();
	
	public ExecutorService getOrCreateAddChannelExecutor() {
		return addChannelExecutor;
	}
	
	@Listener
	public void onPlayerJoin(ServerSideConnectionEvent.Join e, @First ServerPlayer p) {
		addChannel(p);
	}
	
	@Listener
	public void onLeave(ServerSideConnectionEvent.Disconnect e, @First ServerPlayer p) {
		Channel channel = SpongeVersionAdapter.getVersionAdapter().getChannel(p);
		removeChannel(channel, "negativity_decoder");
		removeChannel(channel, "negativity_encoder");
	}
	
	private void addChannel(ServerPlayer p) {
		getOrCreateAddChannelExecutor().execute(() -> {
			Channel channel = SpongeVersionAdapter.getVersionAdapter().getChannel(p);
			try {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore("decoder", "negativity_decoder", new NettyDecoderHandler(SpongeEntityManager.getPlayer(p), PacketDirection.CLIENT_TO_SERVER));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addBefore("encoder", "negativity_encoder", new NettyEncoderHandler(SpongeEntityManager.getPlayer(p), PacketDirection.SERVER_TO_CLIENT));
			} catch (NoSuchElementException exc) {
				// appear when the player's channel isn't accessible because of reload.
				SpongeNegativity.getInstance().getLogger().warn("Please, don't use reload, this can produce some problem. Currently, " + p.name() + " isn't fully checked because of that. More details: " + exc.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException exc) {
				if(exc.getMessage().contains("Duplicate handler")) {
					removeChannel(channel, "negativity_decoder");
					removeChannel(channel, "negativity_encoder");
					addChannel(p);
				} else
					SpongeNegativity.getInstance().getLogger().error("Error while loading Packet channel. " + exc.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}
	
	private void removeChannel(Channel c, String key) {
		if(c.pipeline().get(key) != null)
			c.pipeline().remove(key);
	}
}
