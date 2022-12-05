package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.universal.Adapter;

import io.netty.channel.Channel;

public abstract class NettyPacketListener {

	private static final String ENCODER_KEY = "encoder", ENCODER_KEY_HANDLER = "encoder_negativity";
	private static final String DECODER_KEY = "decoder", DECODER_KEY_HANDLER = "decoder_negativity";

	private static NettyPacketListener instance;

	public static NettyPacketListener getInstance() {
		return instance;
	}

	private ExecutorService channelExecutor = Executors.newSingleThreadExecutor();
	public List<Channel> checked = new ArrayList<>();

	public ExecutorService getChannelExecutor() {
		return channelExecutor;
	}

	public NettyPacketListener() {
		instance = this;
	}

	public void join(Player p) {
		addChannel(p);
	}

	public void left(Player p) {
		Channel channel = getChannel(p);
		removeChannel(channel, DECODER_KEY_HANDLER);
		removeChannel(channel, ENCODER_KEY_HANDLER);
	}

	private void addChannel(Player p) {
		getChannelExecutor().execute(() -> {
			Channel channel = getChannel(p);
			checked.add(channel);
			try {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore(DECODER_KEY, DECODER_KEY_HANDLER, new NettyDecoderHandler(p, PacketDirection.CLIENT_TO_SERVER));

				// Managing outgoing packet (to the player)
				channel.pipeline().addBefore(ENCODER_KEY, ENCODER_KEY_HANDLER, new NettyEncoderHandler(p, PacketDirection.SERVER_TO_CLIENT));
			} catch (NoSuchElementException exc) {
				if (!p.isOnline())
					return; // ignore, just left
				// appear when the player's channel isn't accessible because of reload.
				Adapter.getAdapter().getLogger().warn("Please, don't use reload, this can produce some problem. Currently, " + p.getName()
						+ " isn't fully checked because of that. More details: " + exc.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException exc) {
				if (exc.getMessage().contains("Duplicate handler")) {
					removeChannel(channel, DECODER_KEY_HANDLER);
					removeChannel(channel, ENCODER_KEY_HANDLER);
					addChannel(p);
				} else
					Adapter.getAdapter().getLogger().error("Error while loading Packet channel. " + exc.getMessage() + ". Please, prefer restart than reload.");
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
