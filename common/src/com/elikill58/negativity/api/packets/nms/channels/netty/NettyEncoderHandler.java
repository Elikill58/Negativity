package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.nio.channels.ClosedChannelException;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class NettyEncoderHandler extends MessageToMessageEncoder<ByteBuf> {

	private final Player p;
	
	public NettyEncoderHandler(Player p) {
		this.p = p;
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause.getMessage().toLowerCase().contains("connection reset by ")
				|| cause.getLocalizedMessage().toLowerCase().contains("connection reset by "))
			return;
		if(cause instanceof ClosedChannelException)
			return;
		Adapter.getAdapter().getLogger().error("Exception caught when sending packet");
		cause.printStackTrace();
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		ByteBuf buf = msg.copy();
		int packetId = new PacketSerializer(buf).readVarInt();
		NPacket packet = Adapter.getAdapter().getVersionAdapter().getVersion().getPacket(PacketDirection.SERVER_TO_CLIENT, packetId);
		if(packet == null) {
			out.add(msg.retain());
			return;
		}
		packet.read(new PacketSerializer(buf));
		PacketSendEvent event = new PacketSendEvent(packet, p);
		EventManager.callEvent(event);
		if(!event.isCancelled())
			out.add(msg.retain());
	}
}
