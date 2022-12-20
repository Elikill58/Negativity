package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.plugin.ExternalPlugin;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import io.netty.channel.Channel;

public abstract class NettyPacketListener {

	private static final String ENCODER_KEY = "encoder", ENCODER_KEY_HANDLER = "encoder_negativity";
	private static final String DECODER_KEY = "decoder", DECODER_KEY_HANDLER = "decoder_negativity";

	private static NettyPacketListener instance;

	public static NettyPacketListener getInstance() {
		return instance;
	}

	public List<Channel> checked = new ArrayList<>();

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
		Version version = PlayerVersionManager.getPlayerVersion(p);
		if(version.equals(Version.V1_19_2) && !Adapter.getAdapter().getServerVersion().equals(Version.V1_19_2)) {
			ExternalPlugin plugin = Adapter.getAdapter().getPlugin("ViaVersion");
			if(plugin != null && plugin.getVersion().startsWith("4.5")) {
				Adapter.getAdapter().getLogger().warn("Player " + p.getName() + " can't be checked because of ViaVersion issue.");
				NegativityPlayer.getNegativityPlayer(p).buggedVersion = true;
				return;
			}
		} else if(version.equals(Version.HIGHER) || version.equals(Version.LOWER)) {
			Adapter.getAdapter().getLogger().warn("Player " + p.getName() + " seems to login with unknow version, protocol: " + PlayerVersionManager.getPlayerProtocolVersion(p));
			NegativityPlayer.getNegativityPlayer(p).buggedVersion = true;
			return;
		}
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
	}

	public abstract Channel getChannel(Player p);

	private void removeChannel(Channel c, String key) {
		if (c.pipeline().get(key) != null)
			c.pipeline().remove(key);
	}
}
