package com.elikill58.negativity.spigot.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.nms.channels.netty.NettyPacketListener;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.impl.entity.SpigotEntityManager;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class PacketListeners extends NettyPacketListener implements Listener {

	static final String KEY_HANDLER_PLAYER = "packet_handler", KEY_PLAYER = "packet_player_negativity", KEY_HANDSHAKE = "packet_handshake_negativity",
			KEY_HANDLER_SERVER = "packet_handler", KEY_SERVER = "packet_server_negativity";
	
	public PacketListeners() {
		/*SpigotVersionAdapter.getVersionAdapter().getFuturChannel().forEach((channelFuture) -> {
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
		});*/
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		join(SpigotEntityManager.getPlayer(p));
	}
	
	@EventHandler
	public void onLeft(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(p.hasMetadata("NPC"))
			return;
		left(SpigotEntityManager.getPlayer(p));
	}

	@Override
	public Channel getChannel(com.elikill58.negativity.api.entity.Player p) {
		return SpigotVersionAdapter.getVersionAdapter().getChannel((Player) p.getDefault());
	}

	public class ChannelHandlerHandshakeReceive extends ChannelInboundHandlerAdapter {

		private Channel channel;
		
		public ChannelHandlerHandshakeReceive(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) {
			try {
				if(Bukkit.getOnlinePlayers().stream().filter(p -> SpigotVersionAdapter.getVersionAdapter().getChannel(p) == ctx.channel()).count() > 0 && checked.contains(ctx.channel())) {
					super.channelRead(ctx, packet);
					return;
				}
				Adapter ada = Adapter.getAdapter();
				if(packet.getClass().getSimpleName().equalsIgnoreCase("PacketLoginInEncryptionBegin")) {
					channel.pipeline().addAfter("encoder", "negativity_encoder_test", new ChannelOutboundHandlerAdapter() {

						@Override
						public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
							if(checked.contains(ctx.channel())) {
								super.write(ctx, obj, promise);
								return;
							}
							if(obj instanceof ByteBuf) {
								ByteBuf buf = ((ByteBuf) obj).copy();
								int packetId = new PacketSerializer(buf).readVarInt();
								ada.debug("PacketId: " + packetId + " (type: " + Version.V1_19_2.getNamedVersion().getPacket(PacketDirection.CLIENT_TO_SERVER, packetId).getPacketName() + ")");
							} else
								ada.debug("Obj: " + obj.getClass().getSimpleName());
							super.write(ctx, obj, promise);
						}
					});		
				}
				ada.debug("CTX: " + ctx.name() + ", " + (channel == ctx.channel() ? "same" : "diff: " + channel.getClass().getSimpleName() + " / " + ctx.channel().getClass().getSimpleName()) + ", packet: " + packet.getClass().getSimpleName());
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
