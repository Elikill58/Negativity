package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;
import com.elikill58.negativity.universal.utils.SemVer;

import io.netty.channel.Channel;

public abstract class NettyPacketListener {

	private static final String ENCODER_KEY = "encoder", ENCODER_KEY_HANDLER = "encoder_negativity";
	private static final String DECODER_KEY = "decoder", DECODER_KEY_HANDLER = "decoder_negativity";

	private static NettyPacketListener instance;

	public static NettyPacketListener getInstance() {
		return instance;
	}

	public List<Channel> checked = new ArrayList<>();
	private boolean shouldFixViaversion = false;

	public NettyPacketListener() {
		instance = this;
		Adapter ada = Adapter.getAdapter();
		if(ada.hasPlugin("ViaVersion")) {
			// 4.4.9 doesn't exist but it's latest before 4.5
			shouldFixViaversion = SemVer.parse(ada.getPlugin("ViaVersion").getVersion()).isNewerThan(SemVer.parse("4.4.9"));
		}
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
		Adapter ada = Adapter.getAdapter();
		if(version.equals(Version.HIGHER) || version.equals(Version.LOWER)) {
			ada.getLogger().warn("Player " + p.getName() + " seems to login with unknow version, protocol: " + PlayerVersionManager.getPlayerProtocolVersion(p));
			NegativityPlayer.getNegativityPlayer(p).buggedVersion = true;
			return;
		}
		if(shouldFixViaversion && ada.getPlatformID().equals(Platform.SPIGOT)) { // can have viaversion issue
			Version serverVersion = ada.getServerVersion();
			boolean playerIs19 = version.name().startsWith("V1_19");
			boolean serverIs19 = serverVersion.name().startsWith("V1_19");
			if(playerIs19 && !serverIs19) {
				ada.getLogger().warn("Player " + p.getName() + " have different support because of ViaVersion issue (Player 1.19+ on 1.18- servers).");
				version = serverVersion;
			} else if(serverIs19 && !serverVersion.equals(version)) {
				ada.getLogger().warn("Player " + p.getName() + " have different support because of ViaVersion issue (Player 1.18- on 1.19+ servers).");
				version = serverVersion;
			} else if(version.equals(Version.V1_8) && serverVersion.equals(Version.V1_18))  {
				ada.getLogger().warn("Player " + p.getName() + " have different support because of ViaVersion issue (Player 1.8 on 1.18 servers).");
				version = serverVersion;
				
			}
			p.setPlayerVersion(version);
		}
		Channel channel = getChannel(p);
		boolean hadChannel = checked.contains(channel);
		checked.add(channel);
		try {
			// Managing incoming packet (from player)
			channel.pipeline().addBefore(DECODER_KEY, DECODER_KEY_HANDLER, new NettyDecoderHandler(p, PacketDirection.CLIENT_TO_SERVER, version));

			// Managing outgoing packet (to the player)
			channel.pipeline().addBefore(ENCODER_KEY, ENCODER_KEY_HANDLER, new NettyEncoderHandler(p, PacketDirection.SERVER_TO_CLIENT, version));
		} catch (NoSuchElementException exc) {
			if (!p.isOnline())
				return; // ignore, just left
			// appear when the player's channel isn't accessible because of reload.
			ada.getLogger().warn("Please, don't use reload, this can produce some problem. Currently, " + p.getName()
					+ " isn't fully checked because of that. More details: " + exc.getMessage() + " (NoSuchElementException)." + (!hadChannel ? "Trying again ..." : ""));
			if(!hadChannel)
				Adapter.getAdapter().getScheduler().runDelayed(() -> addChannel(p), 10);
		} catch (IllegalArgumentException exc) {
			if (exc.getMessage().contains("Duplicate handler")) {
				removeChannel(channel, DECODER_KEY_HANDLER);
				removeChannel(channel, ENCODER_KEY_HANDLER);
				addChannel(p);
			} else
				ada.getLogger().error("Error while loading Packet channel. " + exc.getMessage() + ". Please, prefer restart than reload.");
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
