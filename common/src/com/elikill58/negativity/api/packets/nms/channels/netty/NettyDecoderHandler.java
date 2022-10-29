package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.nio.channels.ClosedChannelException;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyDecoderHandler extends ChannelInboundHandlerAdapter {

	private final Player p;
	private final PacketDirection direction;
	
	public NettyDecoderHandler(Player p, PacketDirection direction) {
		this.p = p;
		this.direction = direction;
	}
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (msg instanceof ByteBuf) {
			ByteBuf buf = ((ByteBuf) msg).copy();
			int packetId = new PacketSerializer(buf).readVarInt();
			NPacket packet = Adapter.getAdapter().getVersionAdapter().getVersion().getPacket(direction, packetId);
			if(packet == null) {
				super.channelRead(ctx, msg);
				return;
			}
			packet.read(new PacketSerializer(buf));
			PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
			EventManager.callEvent(event);
			if(!event.isCancelled())
				super.channelRead(ctx, msg);
		} else
			super.channelRead(ctx, msg);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause instanceof ClosedChannelException)
			return;
		super.exceptionCaught(ctx, cause);
	}
}
