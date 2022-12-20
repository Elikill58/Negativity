package com.elikill58.negativity.api.packets.nms.channels.netty;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyDecoderHandler extends ChannelInboundHandlerAdapter {

	private final Player p;
	private final PacketDirection direction;
	private final Version version;

	public NettyDecoderHandler(Player p, PacketDirection direction) {
		this.p = p;
		this.direction = direction;
		this.version = PlayerVersionManager.getPlayerVersion(p);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(!p.isOnline()) {
			NettyPacketListener.getInstance().left(p);
			return;
		}
		NettyHandlerCommon.manageError(ctx, cause, "receiving");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		try {
			if (obj instanceof ByteBuf) {
				ByteBuf msg = ((ByteBuf) obj).copy();
				NPacket packet = NettyHandlerCommon.readPacketFromByteBuf(p, version, direction, msg, "decode");
				if(packet != null) {
					PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
					EventManager.callEvent(event);
					if (event.isCancelled())
						return;
				}
			}
		} catch (Throwable e) { // manage error myself to let everything continue
			NettyHandlerCommon.manageError(ctx, e, "internal receiving");
		}
		super.channelRead(ctx, obj);
	}
}
