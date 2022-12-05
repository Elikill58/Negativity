package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public abstract class NettyPacketListener {

	private static final String KEY_HANDSHAKE = "global_decoder_negativity";
	private static final String ENCODER_KEY = "encoder", ENCODER_KEY_HANDLER = "encoder_negativity";
	private static final String DECODER_KEY = "decoder", DECODER_KEY_HANDLER = "decoder_negativity";

	private static NettyPacketListener instance;

	public static NettyPacketListener getInstance() {
		return instance;
	}

	private ExecutorService channelExecutor = Executors.newSingleThreadExecutor();
	public List<Channel> checked = new ArrayList<>();

	public ExecutorService getChannelExecutor() {
		return channelExecutor;
	}

	public NettyPacketListener() {
		this(new ArrayList<>());
	}

	public NettyPacketListener(List<ChannelFuture> channels) {
		instance = this;
		channels.forEach((channelFuture) -> {
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
											channel.pipeline().addBefore("decoder", KEY_HANDSHAKE, interceptor);
										}
										return interceptor;
									} catch (IllegalArgumentException e) {
										// Try again
										return channel.pipeline().get(KEY_HANDSHAKE);
									}
								});
							} catch (Exception e) {
								Adapter.getAdapter().getLogger().printError("Cannot inject incomming channel " + channel, e);
							}
						}
					});
					ctx.fireChannelRead(msg);
				}
			});
		});
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
		getChannelExecutor().execute(() -> {
			Channel channel = getChannel(p);
			checked.add(channel);
			try {
				// Managing incoming packet (from player)
				channel.pipeline().addBefore(DECODER_KEY, DECODER_KEY_HANDLER, new NettyDecoderHandler(p, PacketDirection.CLIENT_TO_SERVER));

				// Managing outgoing packet (to the player)
				channel.pipeline().addBefore(ENCODER_KEY, ENCODER_KEY_HANDLER, new NettyEncoderHandler(p, PacketDirection.SERVER_TO_CLIENT));
			} catch (NoSuchElementException exc) {
				if (!p.isOnline())
					return; // ignore, just left
				// appear when the player's channel isn't accessible because of reload.
				Adapter.getAdapter().getLogger().warn("Please, don't use reload, this can produce some problem. Currently, " + p.getName()
						+ " isn't fully checked because of that. More details: " + exc.getMessage() + " (NoSuchElementException)");
			} catch (IllegalArgumentException exc) {
				if (exc.getMessage().contains("Duplicate handler")) {
					removeChannel(channel, DECODER_KEY_HANDLER);
					removeChannel(channel, ENCODER_KEY_HANDLER);
					addChannel(p);
				} else
					Adapter.getAdapter().getLogger().error("Error while loading Packet channel. " + exc.getMessage() + ". Please, prefer restart than reload.");
			} catch (Exception exc) {
				exc.printStackTrace();
			}
		});
	}

	public abstract Channel getChannel(Player p);

	private void removeChannel(Channel c, String key) {
		if (c.pipeline().get(key) != null)
			c.pipeline().remove(key);
	}

	public class ChannelHandlerHandshakeReceive extends ChannelInboundHandlerAdapter {

		private Channel channel;

		public ChannelHandlerHandshakeReceive(Channel channel) {
			this.channel = channel;
		}

		@Override
		public void channelRead(ChannelHandlerContext ctx, Object packet) {
			try {
				if (checked.contains(ctx.channel())) {
					super.channelRead(ctx, packet);
					return;
				}
				Adapter ada = Adapter.getAdapter();
				if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketLoginInEncryptionBegin")) {
					channel.pipeline().addAfter("encoder", "negativity_encoder_test", new ChannelOutboundHandlerAdapter() {

						@Override
						public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
							if (checked.contains(ctx.channel())) {
								super.write(ctx, obj, promise);
								return;
							}
							if (obj instanceof ByteBuf) {
								ByteBuf buf = ((ByteBuf) obj).copy();
								int packetId = new PacketSerializer(buf).readVarInt();
								ada.debug(
										"PacketId: " + packetId + " (type: " + Version.V1_19_2.getNamedVersion().getPacket(PacketDirection.CLIENT_TO_SERVER, packetId).getPacketName() + ")");
							} else
								ada.debug("Obj: " + obj.getClass().getSimpleName());
							super.write(ctx, obj, promise);
						}
					});
				} else if(packet instanceof ByteBuf) {
					ByteBuf buf = ((ByteBuf) packet).copy();
					PacketSerializer serializer = new PacketSerializer(buf);
					int packetId = serializer.readVarInt();
					ada.debug("Packet Id: " + packetId);
				}
				ada.debug("CTX: " + ctx.name() + ", " + (channel == ctx.channel() ? "same" : "diff: " + channel.getClass().getSimpleName() + " / " + ctx.channel().getClass().getSimpleName())
						+ ", packet: " + packet.getClass().getSimpleName());
				super.channelRead(ctx, packet);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().error("Error while reading packet : " + e.getMessage());
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
