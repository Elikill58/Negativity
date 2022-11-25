package com.elikill58.negativity.api.packets.nms.channels.netty;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class NettyEncoderHandler extends ChannelOutboundHandlerAdapter {

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
		NettyHandlerCommon.manageError(ctx, cause, "sending");
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object obj, ChannelPromise promise) throws Exception {
		if(obj instanceof ByteBuf) {
			ByteBuf msg = (ByteBuf) obj;
			NPacket packet = NettyHandlerCommon.readPacketFromByteBuf(p, version, direction, ctx, msg.copy(), "encode");
			if(packet == null) {
				super.write(ctx, msg, promise);
				return;
			}
			PacketSendEvent event = new PacketSendEvent(packet, p);
			EventManager.callEvent(event);
			if(!event.isCancelled())
				super.write(ctx, msg, promise);
		}
	}
}