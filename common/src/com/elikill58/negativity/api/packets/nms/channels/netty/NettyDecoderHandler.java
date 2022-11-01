package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.nio.channels.ClosedChannelException;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class NettyDecoderHandler extends MessageToMessageDecoder<ByteBuf> {

	private final Player p;
	private final PacketDirection direction;
	
	public NettyDecoderHandler(Player p, PacketDirection direction) {
		this.p = p;
		this.direction = direction;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause.getMessage().toLowerCase().contains("connection reset by ")
				|| cause.getLocalizedMessage().toLowerCase().contains("connection reset by "))
			return;
		if(cause instanceof ClosedChannelException)
			return;
		Adapter.getAdapter().getLogger().error("Exception caught when receiving packet");
		cause.printStackTrace();
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		ByteBuf buf = ((ByteBuf) msg).copy();
		int packetId = new PacketSerializer(buf).readVarInt();
		NPacket packet = Adapter.getAdapter().getVersionAdapter().getVersion().getPacket(direction, packetId);
		if(packet == null) {
			out.add(msg.retain());
			return;
		}
		packet.read(new PacketSerializer(buf));
		PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
		EventManager.callEvent(event);
		if(!event.isCancelled())
			out.add(msg.retain());
	}
}
