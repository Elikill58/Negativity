package com.elikill58.negativity.spigot.packets.custom.channel;

import static com.elikill58.negativity.spigot.utils.PacketUtils.getPlayerConnection;

import java.lang.reflect.Field;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.packets.AbstractPacket;
import com.elikill58.negativity.spigot.packets.custom.CustomPacketManager;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.universal.PacketType;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelFuture;
import net.minecraft.util.io.netty.channel.ChannelHandlerContext;
import net.minecraft.util.io.netty.channel.ChannelInboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelInitializer;
import net.minecraft.util.io.netty.channel.ChannelOutboundHandlerAdapter;
import net.minecraft.util.io.netty.channel.ChannelPromise;

public class NMUChannel extends ChannelAbstract {

	public NMUChannel(CustomPacketManager customPacketManager) {
		super(customPacketManager);
		try {
			Object dedicatedSrv = PacketUtils.getCraftServer();
			Object mcServer = dedicatedSrv.getClass().getMethod("getServer").invoke(dedicatedSrv);
			Object co = ReflectionUtils.getFirstWith(mcServer, PacketUtils.getNmsClass("MinecraftServer", "server."), PacketUtils.getNmsClass("ServerConnection", "network."));
			@SuppressWarnings("unchecked")
			List<ChannelFuture> g = (List<ChannelFuture>) ReflectionUtils.getField(co, "g");
			g.forEach((channelFuture) -> {
				channelFuture.channel().pipeline().addFirst(new ChannelInboundHandlerAdapter() {
					@Override
					public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
						((Channel) msg).pipeline().addFirst(new ChannelInitializer<Channel>() {
							@Override
							protected void initChannel(Channel channel) {
								try {
									channel.eventLoop().submit(() -> {
										try {
											ChannelHandlerHandshakeReceive interceptor = (ChannelHandlerHandshakeReceive) channel.pipeline().get(KEY_HANDSHAKE);
											// Inject our packet interceptor
											if (interceptor == null) {
												interceptor = new ChannelHandlerHandshakeReceive();
												channel.pipeline().addBefore("packet_handler", KEY_HANDSHAKE, interceptor);
											}
											return interceptor;
										} catch (IllegalArgumentException e) {
											// Try again
											return (ChannelHandlerHandshakeReceive) channel.pipeline().get(KEY_HANDSHAKE);
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

	private Channel getChannel(Player p) {
		try {
			Object playerConnection = getPlayerConnection(p);
			Object networkManager = playerConnection.getClass().getField("networkManager").get(playerConnection);
			
			for (Field field : networkManager.getClass().getDeclaredFields())
				if (field.getType().equals(Channel.class)) {
					field.setAccessible(true);
					return (Channel) field.get(networkManager);
				}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketReceive(PacketType.getType(packet.getClass().getSimpleName()), this.owner, packet);
			if(nextPacket != null && nextPacket.isCancelled())
				return;
			super.channelRead(ctx, packet);
		}
	}

	private class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}
		
		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			AbstractPacket nextPacket = getPacketManager().onPacketSent(PacketType.getType(packet.getClass().getSimpleName()), owner, packet);
			if(nextPacket != null && nextPacket.isCancelled())
				return;
			super.write(ctx, packet, promise);
		}
	}

	private class ChannelHandlerHandshakeReceive extends ChannelInboundHandlerAdapter {

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) {
			try {
				PacketType packetType = PacketType.getType(packet.getClass().getSimpleName());
				if(packetType instanceof PacketType.Client || packetType instanceof PacketType.Server)
					return;
				//SpigotNegativity.getInstance().getLogger().info("PacketType: " + packet.getClass().getSimpleName() + " > " + packetType);
				AbstractPacket nextPacket = getPacketManager().onPacketReceive(packetType, null, packet);
				if(nextPacket != null && nextPacket.isCancelled())
					return;
				super.channelRead(ctx, packet);
			} catch (Exception e) {
				SpigotNegativity.getInstance().getLogger().severe("Error while reading packet : " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
