package com.elikill58.negativity.sponge7.nms;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.PlayerConnection;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyChannel;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;

public class SpongeVersionAdapter extends VersionAdapter<Player> {
	
	public SpongeVersionAdapter() {
		super(Adapter.getAdapter().getServerVersion());
	}

	public Channel getChannel(Player p) {
		try {
			PlayerConnection co = p.getConnection();
			Object networkManager = co.getClass().getDeclaredField("field_147371_a").get(co);
			return ReflectionUtils.getFirstWith(networkManager, networkManager.getClass(), Channel.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AbstractChannel getPlayerChannel(Player p) {
		Channel channel = getChannel(p);
		return channel == null ? null : new NettyChannel(channel);
	}
	
	private static SpongeVersionAdapter instance = new SpongeVersionAdapter();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
