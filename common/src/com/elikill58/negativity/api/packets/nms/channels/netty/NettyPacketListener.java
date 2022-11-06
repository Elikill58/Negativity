package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.universal.Adapter;

import io.netty.channel.Channel;

public abstract class NettyPacketListener {

	private ExecutorService channelExecutor = Executors.newSingleThreadExecutor();

	public ExecutorService getOrCreatChannelExecutor() {
		return channelExecutor;
	}

	public void join(Player p) {
		addChannel(p);
	}

	public void left(Player p) {
		Channel channel = getChannel(p);
		removeChannel(channel, "negativity_decoder");
		removeChannel(channel, "negativity_encoder");
	}

	private void addChannel(Player p) {
		getOrCreatChannelExecutor().execute(() -> {
			Channel channel = getChannel(p);
			try {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore("decoder", "negativity_decoder",
						new NettyDecoderHandler(p, PacketDirection.CLIENT_TO_SERVER));

				// Managing outgoing packet (to the player)
				channel.pipeline().addBefore("encoder", "negativity_encoder",
						new NettyEncoderHandler(p, PacketDirection.SERVER_TO_CLIENT));
			} catch (NoSuchElementException exc) {
				// appear when the player's channel isn't accessible because of reload.
				Adapter.getAdapter().getLogger()
						.warn("Please, don't use reload, this can produce some problem. Currently, " + p.getName()
								+ " isn't fully checked because of that. More details: " + exc.getMessage()
								+ " (NoSuchElementException)");
			} catch (IllegalArgumentException exc) {
				if (exc.getMessage().contains("Duplicate handler")) {
					removeChannel(channel, "negativity_decoder");
					removeChannel(channel, "negativity_encoder");
					addChannel(p);
				} else
					Adapter.getAdapter().getLogger().error("Error while loading Packet channel. " + exc.getMessage()
							+ ". Please, prefer restart than reload.");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}

	public abstract Channel getChannel(Player p);

	private void removeChannel(Channel c, String key) {
		if (c.pipeline().get(key) != null)
			c.pipeline().remove(key);
	}
}
