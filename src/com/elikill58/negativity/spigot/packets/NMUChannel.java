package com.elikill58.negativity.spigot.packets;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.PacketAbstract.IPacketListener;
import com.elikill58.negativity.spigot.utils.Utils;

import net.minecraft.util.io.netty.channel.ChannelHandlerContext;

public class NMUChannel extends ChannelAbstract {
	
	public NMUChannel(IPacketListener iPacketListener) {
		super(iPacketListener);
	}

	@Override
	public void addChannel(final Player player) {
		try {
			final net.minecraft.util.io.netty.channel.Channel channel = getChannel(player);
			addChannelExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						channel.pipeline().addBefore(KEY_HANDLER, KEY_PLAYER, new ChannelHandler(player));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void removeChannel(Player player) {
		try {
			final net.minecraft.util.io.netty.channel.Channel channel = getChannel(player);
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

	private net.minecraft.util.io.netty.channel.Channel getChannel(Player p) {
		try {
			Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".entity.CraftPlayer")
					.cast(p);
			Object entityPlayer = craftPlayer.getClass().getMethod("getHandle", new Class[0]).invoke(craftPlayer,
					new Object[0]);
			Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			
			for (Field field : networkManager.getClass().getDeclaredFields())
				if (field.getType().equals(net.minecraft.util.io.netty.channel.Channel.class)) {
					field.setAccessible(true);
					return (net.minecraft.util.io.netty.channel.Channel) field.get(networkManager);
				}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	class ChannelHandler extends net.minecraft.util.io.netty.channel.ChannelDuplexHandler {

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
