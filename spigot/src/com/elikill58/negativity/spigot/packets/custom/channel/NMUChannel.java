package com.elikill58.negativity.spigot.packets.custom.channel;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelFuture;
import net.minecraft.util.io.netty.channel.ChannelHandler;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelInitializer;
import net.minecraft.util.io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelPromise;

public class NMUChannel extends ChannelAbstract {

	@SuppressWarnings("unchecked")
	public NMUChannel(CustomPacketManager customPacketManager) {
		super(customPacketManager);
		try {
			Object mcServer = PacketUtils.getDedicatedServer();
			Object co = ReflectionUtils.getFirstWith(mcServer, PacketUtils.getNmsClass("MinecraftServer"), PacketUtils.getNmsClass("ServerConnection"));
			((List<ChannelFuture>) ReflectionUtils.getField(co, "e")).forEach((channelFuture) -> {
				channelFuture.channel().pipeline().addFirst(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						((Channel) msg).pipeline().addFirst(new ChannelInitializer<Channel>() {
							@Override
							protected void initChannel(Channel channel) {
								try {
									channel.eventLoop().submit(() -> {
										try {
											ChannelHandler interceptor = channel.pipeline().get(KEY_HANDSHAKE);
											// Inject our packet interceptor
											if (interceptor == null) {
												interceptor = new ChannelHandlerHandshakeReceive(channel);
												channel.pipeline().addBefore("packet_handler", KEY_HANDSHAKE, interceptor);
											}
											return interceptor;
										} catch (IllegalArgumentException e) {
											// Try again
											return channel.pipeline().get(KEY_HANDSHAKE);
										}
									});
								} catch (Exception e) {
									getPacketManager().getPlugin().getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
								}
							}
						});
						ctx.fireChannelRead(msg);
					}
				});
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void addChannel(final Player player, String endChannelName) {
		getOrCreateAddChannelExecutor().execute(() -> {
			try {
				Channel channel = getChannel(player);
				// Managing incoming packet (from player)
				channel.pipeline().addBefore(KEY_HANDLER_PLAYER, KEY_PLAYER + endChannelName, new ChannelHandlerReceive(player));
				
				// Managing outgoing packet (to the player)
				channel.pipeline().addAfter(KEY_HANDLER_SERVER, KEY_SERVER + endChannelName, new ChannelHandlerSent(player));
			} catch (NoSuchElementException e) {
				// appear when the player's channel isn't accessible because of reload.
				getPacketManager().getPlugin().getLogger().warning("Please, don't use reload, this can produce some problem. Currently, " + player.getName() + " isn't fully checked because of that. More details: " + e.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException e) {
				if(e.getMessage().contains("Duplicate")) {
					removeChannel(player, endChannelName);
					addChannel(player, endChannelName);
				} else
					getPacketManager().getPlugin().getLogger().severe("Error while loading Packet channel. " + e.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void removeChannel(Player player, String endChannelName) {
		getOrCreateRemoveChannelExecutor().execute(() -> {
			try {
				final Channel channel = getChannel(player);
				
				if(channel.pipeline().get(KEY_PLAYER + endChannelName) != null)
					channel.pipeline().remove(KEY_PLAYER + endChannelName);
				
				if(channel.pipeline().get(KEY_SERVER + endChannelName) != null)
					channel.pipeline().remove(KEY_SERVER + endChannelName);
			} catch (Exception e) {}
		});
	}

	@Override
	public Channel getChannel(Player p) throws Exception {
		Object playerConnection = SpigotVersionAdapter.getVersionAdapter().getPlayerConnection(p);
		Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
		
		for (Field field : networkManager.getClass().getDeclaredFields())
			if (field.getType().equals(Channel.class)) {
				field.setAccessible(true);
				return (Channel) field.get(networkManager);
			}
		return null;
	}

	private class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(owner, packet);
			AbstractPacket nextPacket = getPacketManager().onPacketReceive(commonPacket, SpigotEntityManager.getPlayer(this.owner), packet);
			if(!nextPacket.isCancelled() && nextPacket.getNmsPacket() != null)
				super.channelRead(ctx, nextPacket.getNmsPacket());
		}
	}

	private class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}
		
		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(owner, packet);
			AbstractPacket nextPacket = getPacketManager().onPacketSent(commonPacket, SpigotEntityManager.getPlayer(this.owner), packet);
			if(!nextPacket.isCancelled() && nextPacket.getNmsPacket() != null)
				super.write(ctx, nextPacket.getNmsPacket(), promise);
		}
	}

	private class ChannelHandlerHandshakeReceive extends ChannelInboundHandlerAdapter {

		private Channel channel;
		
		public ChannelHandlerHandshakeReceive(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) {
			try {
				PacketType packetType = PacketType.getType(packet.getClass().getSimpleName());
				if(!(packetType instanceof PacketType.Client || packetType instanceof PacketType.Server)) {
					NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(null, packet);
					AbstractPacket nextPacket = getPacketManager().onPacketReceive(commonPacket, null, packet);
					if(nextPacket != null && nextPacket.isCancelled())
						return;
					if(commonPacket instanceof NPacketHandshakeInSetProtocol)
						getPacketManager().protocolVersionPerChannel.put(channel, ((NPacketHandshakeInSetProtocol) commonPacket).procotol);
				}
				super.channelRead(ctx, packet);
			} catch (Exception e) {
				SpigotNegativity.getInstance().getLogger().severe("Error while reading packet : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
