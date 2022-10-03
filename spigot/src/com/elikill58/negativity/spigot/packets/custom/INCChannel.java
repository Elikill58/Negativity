package com.elikill58.negativity.spigot.packets.custom;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.handshake.NPacketHandshakeInSetProtocol;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Adapter;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class INCChannel {

	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_player_negativity", KEY_HANDSHAKE = "packet_handshake_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_server_negativity";
	
	private ExecutorService addChannelExecutor, removeChannelExecutor;
	private final CustomPacketManager customPacketManager;
	private HashSet<UUID> players = new HashSet<>();
	
	public INCChannel(CustomPacketManager customPacketManager) {
		this.customPacketManager = customPacketManager;
		SpigotVersionAdapter.getVersionAdapter().getFuturChannel().forEach((channelFuture) -> {
			channelFuture.channel().pipeline().addFirst(new ChannelInboundHandlerAdapter() {
				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
					ctx.fireChannelRead(msg);
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
								SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Cannot inject incomming channel " + channel, e);
							}
						}
					});
				}
			});
		});
	}
	
	public CustomPacketManager getPacketManager() {
		return customPacketManager;
	}
	
	public ExecutorService getAddChannelExecutor() {
		return addChannelExecutor;
	}
	
	public ExecutorService getOrCreateAddChannelExecutor() {
		if(addChannelExecutor == null)
			addChannelExecutor = Executors.newSingleThreadExecutor();
		return addChannelExecutor;
	}
	
	public ExecutorService getRemoveChannelExecutor() {
		return removeChannelExecutor;
	}
	
	public ExecutorService getOrCreateRemoveChannelExecutor() {
		if(removeChannelExecutor == null)
			removeChannelExecutor = Executors.newSingleThreadExecutor();
		return removeChannelExecutor;
	}

	public void addPlayer(Player p) {
		if(players.add(p.getUniqueId())) {
			addChannel(p, p.getUniqueId().toString());
			try {
				Integer protocol = customPacketManager.protocolVersionPerChannel.remove(getChannel(p));
				if(protocol != null)
					SpigotEntityManager.getPlayer(p).setProtocolVersion(protocol);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void removePlayer(Player p) {
		if(players.remove(p.getUniqueId()))
			removeChannel(p, p.getUniqueId().toString());
	}

	public void addChannel(final Player player, String endChannelName) {
		if(!player.isOnline() || player.hasMetadata("NPC"))
			return;
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
				if(e.getMessage().contains("Duplicate handler")) {
					removeChannel(player, endChannelName);
					addChannel(player, endChannelName);
				} else
					getPacketManager().getPlugin().getLogger().severe("Error while loading Packet channel. " + e.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

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

	public Channel getChannel(Player p) {
		return SpigotVersionAdapter.getVersionAdapter().getPlayerChannel(p);
	}

	private class ChannelHandlerReceive extends ChannelInboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerReceive(Player player) {
			this.owner = player;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(owner, PacketDirection.CLIENT_TO_SERVER, packet);
			if(commonPacket == null) {
				super.channelRead(ctx, packet);
				return;
			}
			AbstractPacket nextPacket = getPacketManager().onPacketReceive(commonPacket, SpigotEntityManager.getPlayer(this.owner), packet);
			if(!nextPacket.isCancelled())
				super.channelRead(ctx, nextPacket.getNmsPacket() != null ? nextPacket.getNmsPacket() : packet);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			if(ctx == null || ctx.isRemoved())
				return;
			if(cause.getMessage().toLowerCase().contains("connection reset by ")
					|| cause.getLocalizedMessage().toLowerCase().contains("connection reset by "))
				return;
			Adapter.getAdapter().getLogger().error("Exception caught when reading packet");
			cause.printStackTrace();
		}
	}

	private class ChannelHandlerSent extends ChannelOutboundHandlerAdapter {

		private final Player owner;

		public ChannelHandlerSent(Player player) {
			this.owner = player;
		}

		@Override
		public void write(ChannelHandlerContext ctx, Object packet, ChannelPromise promise) throws Exception {
			NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket(owner, PacketDirection.SERVER_TO_CLIENT, packet);
			if(commonPacket == null) {
				super.write(ctx, packet, promise);
				return;
			}
			AbstractPacket nextPacket = getPacketManager().onPacketSent(commonPacket, SpigotEntityManager.getPlayer(this.owner), packet);
			if(!nextPacket.isCancelled())
				super.write(ctx, nextPacket.getNmsPacket() != null ? nextPacket.getNmsPacket() : packet, promise);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
			if(cause.getMessage().toLowerCase().contains("connection reset by ")
					|| cause.getLocalizedMessage().toLowerCase().contains("connection reset by "))
				return;
			Adapter.getAdapter().getLogger().error("Exception caught when sending packet");
			cause.printStackTrace();
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
					NPacket commonPacket = SpigotVersionAdapter.getVersionAdapter().getPacket((Player) null, PacketDirection.HANDSHAKE, packet);
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
				try {
					super.channelRead(ctx, packet); // even if an issue appear the packet is read
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
