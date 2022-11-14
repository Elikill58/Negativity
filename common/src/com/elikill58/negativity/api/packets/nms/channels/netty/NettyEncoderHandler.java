package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class NettyEncoderHandler extends MessageToMessageEncoder<ByteBuf> {

	private final Player p;
	private final PacketDirection direction;
	private final Version version;
	
	public NettyEncoderHandler(Player p, PacketDirection direction) {
		this.p = p;
		this.direction = direction;
		this.version = PlayerVersionManager.getPlayerVersion(p);
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		NettyHandlerCommon.manageError(ctx, cause);
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
		NPacket packet = NettyHandlerCommon.readPacketFromByteBuf(p, version, direction, ctx, msg.copy(), "encode");
		if(packet == null) {
			out.add(msg.retain());
			return;
		}
		PacketSendEvent event = new PacketSendEvent(packet, p);
		EventManager.callEvent(event);
		if(!event.isCancelled())
			out.add(msg.retain());
	}
}
