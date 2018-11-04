package com.elikill58.negativity.spigot.packets;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.packets.ChannelInjector.ChannelWrapper;
import com.elikill58.negativity.spigot.packets.PacketAbstract.IPacketListener;
import com.elikill58.negativity.spigot.reflection.Resolver;
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
			return (net.minecraft.util.io.netty.channel.Channel) Resolver.resolveByFirstType(networkManager.getClass(), net.minecraft.util.io.netty.channel.Channel.class).get(networkManager);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("serial")
	class ListenerList<E> extends ArrayList<E> {

		@Override
		public boolean add(E paramE) {
			try {
				final E a = paramE;
				addChannelExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							net.minecraft.util.io.netty.channel.Channel channel = null;
							while (channel == null)
								channel = (net.minecraft.util.io.netty.channel.Channel) Resolver.resolveByFirstType(Class.forName("net.minecraft.server." + Utils.VERSION + ".NetworkManager"), net.minecraft.util.io.netty.channel.Channel.class).get(a);
							if (channel.pipeline().get(KEY_SERVER) == null)
								channel.pipeline().addBefore(KEY_HANDLER, KEY_SERVER,
										new ChannelHandler(new NMUChannelWrapper(channel)));
						} catch (Exception e) {
						}
					}
				});
			} catch (Exception e) {
			}
			return super.add(paramE);
		}

		@Override
		public boolean remove(Object arg0) {
			try {
				final Object a = arg0;
				removeChannelExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							net.minecraft.util.io.netty.channel.Channel channel = null;
							while (channel == null)
								channel = (net.minecraft.util.io.netty.channel.Channel) Resolver.resolveByFirstType(Class.forName("net.minecraft.server." + Utils.VERSION + ".NetworkManager"), net.minecraft.util.io.netty.channel.Channel.class).get(a);
							channel.pipeline().remove(KEY_SERVER);
						} catch (Exception e) {
						}
					}
				});
			} catch (Exception e) {
			}
			return super.remove(arg0);
		}
	}

	class ChannelHandler extends net.minecraft.util.io.netty.channel.ChannelDuplexHandler {

		private Object owner;

		public ChannelHandler(Player player) {
			this.owner = player;
		}

		public ChannelHandler(ChannelWrapper<?> channelWrapper) {
			this.owner = channelWrapper;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			Object pckt = msg;
			if (Class.forName("net.minecraft.server." + Utils.VERSION + ".Packet").isAssignableFrom(msg.getClass()))
				pckt = onPacketReceive(this.owner, msg);
			super.channelRead(ctx, pckt);
		}
	}

	class NMUChannelWrapper extends ChannelWrapper<net.minecraft.util.io.netty.channel.Channel> {

		public NMUChannelWrapper(net.minecraft.util.io.netty.channel.Channel channel) {
			super(channel);
		}
	}
}
