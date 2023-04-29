package com.elikill58.negativity.api.packets.nms.channels.netty;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketPreReceiveEvent;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class NettyDecoderHandler extends ChannelInboundHandlerAdapter {

	private final Player p;
	private final PacketDirection direction;
	private final Version version;
	private final NegativityPlayer np;

	public NettyDecoderHandler(Player p, PacketDirection direction, Version version) {
		this.p = p;
		this.direction = direction;
		this.version = version;
		this.np = NegativityPlayer.getNegativityPlayer(p);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(!p.isOnline()) {
			NettyPacketListener.getInstance().left(p);
			return;
		}
		NettyHandlerCommon.manageError(ctx, cause, "receiving");
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		if(getNegativityPlayer().isDisconnecting())
			return; // cancel packets
		try {
			if (obj instanceof ByteBuf) {
				ByteBuf msg = ((ByteBuf) obj).copy();
				NPacket packet = NettyHandlerCommon.readPacketFromByteBuf(p, version, direction, msg, "decode");
				if(packet != null) {
					PacketPreReceiveEvent event = new PacketPreReceiveEvent(packet, p);
					EventManager.callEvent(event);
					if (event.isCancelled())
						return;
					super.channelRead(ctx, obj); // call before use
					getNegativityPlayer().getExecutor().submit(() -> EventManager.callEvent(new PacketReceiveEvent(packet, p)));
				} else
					super.channelRead(ctx, obj); 
				msg.release();
			} else
				super.channelRead(ctx, obj); 
		} catch (Throwable e) { // manage error myself to let everything continue
			NettyHandlerCommon.manageError(ctx, e, "internal receiving");
		}
	}
}
