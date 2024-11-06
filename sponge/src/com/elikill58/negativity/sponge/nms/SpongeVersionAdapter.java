package com.elikill58.negativity.sponge.nms;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.nms.channels.AbstractChannel;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyChannel;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import io.netty.channel.Channel;

public class SpongeVersionAdapter extends VersionAdapter<ServerPlayer> {
	
	private Field connectionField;
	private Method getConnectionMethod;
	
	public SpongeVersionAdapter() {
		super(Adapter.getAdapter().getServerVersion());
		try {
			connectionField = Class.forName("net.minecraft.server.level.ServerPlayer").getDeclaredField("connection");
			getConnectionMethod = Class.forName("net.minecraft.server.network.ServerGamePacketListenerImpl").getDeclaredMethod("getConnection");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Channel getChannel(ServerPlayer p) {
		try {
			//Connection co = ((net.minecraft.server.level.ServerPlayer) p).connection.getConnection();
			Object playerConnection = connectionField.get(p);
			Object co = getConnectionMethod.invoke(playerConnection);
			Channel channel = ReflectionUtils.getFirstWith(co, co.getClass(), Channel.class);
			return channel;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public AbstractChannel getPlayerChannel(ServerPlayer p) {
		Channel channel = getChannel(p);
		return channel == null ? null : new NettyChannel(channel);
	}
	
	private static SpongeVersionAdapter instance = new SpongeVersionAdapter();
	
	public static SpongeVersionAdapter getVersionAdapter() {
		return instance;
	}
}
