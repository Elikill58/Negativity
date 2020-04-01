package com.elikill58.negativity.spigot.packets;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.PacketAbstract.IPacketListener;
import com.elikill58.negativity.spigot.utils.Utils;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public class INCChannel extends ChannelAbstract {

	public INCChannel(IPacketListener iPacketListener) {
		super(iPacketListener);
	}

	@Override
	public void addChannel(final Player player) {
		try {
			final io.netty.channel.Channel channel = getChannel(player);
			addChannelExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						channel.pipeline().addBefore(KEY_HANDLER, KEY_PLAYER, new ChannelHandler(player));
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}
	}

	@Override
	public void removeChannel(Player player) {
		try {
			final io.netty.channel.Channel channel = getChannel(player);
			removeChannelExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						if (channel.pipeline().get(KEY_PLAYER) != null) {
							channel.pipeline().remove(KEY_PLAYER);
						}
					} catch (Exception e) {
					}
				}
			});
		} catch (Exception e) {
		}
	}

	io.netty.channel.Channel getChannel(Player p) throws ReflectiveOperationException {
		try {
			Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".entity.CraftPlayer")
					.cast(p);
			Object entityPlayer = craftPlayer.getClass().getMethod("getHandle", new Class[0]).invoke(craftPlayer,
					new Object[0]);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			return (io.netty.channel.Channel) networkManager.getClass().getDeclaredField("channel").get(networkManager);
		} catch (Exception e) {
			return null;
		}
	}

	class ChannelHandler extends ChannelDuplexHandler {

		private Object owner;

		public ChannelHandler(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			Object pckt = msg;
			if (Class.forName("net.minecraft.server." + Utils.VERSION + ".Packet").isAssignableFrom(msg.getClass()))
				pckt = onPacketReceive(this.owner, msg);
			super.channelRead(ctx, pckt);
		}
	}
}
