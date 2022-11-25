package com.elikill58.negativity.api.packets.nms.channels.netty;

import com.elikill58.negativity.api.NegativityPlayer;
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
	private NegativityPlayer np;

	public NettyDecoderHandler(Player p, PacketDirection direction) {
		this.p = p;
		this.direction = direction;
		this.version = PlayerVersionManager.getPlayerVersion(p);
		this.np = NegativityPlayer.getNegativityPlayer(p);
	}

	public NegativityPlayer getNegativityPlayer() {
		if (np == null) {
			this.np = NegativityPlayer.getNegativityPlayer(p);
		}
		return np;
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		NettyHandlerCommon.manageError(ctx, cause, "receiving");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
		if (obj instanceof ByteBuf) {
			ByteBuf msg = (ByteBuf) obj;
			NPacket packet = NettyHandlerCommon.readPacketFromByteBuf(p, version, direction, ctx, msg.copy(), "decode");
			if (packet == null) {
				super.channelRead(ctx, msg);
				return;
			}
			PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
			EventManager.callEvent(event);
			if (!event.isCancelled()) {
				super.channelRead(ctx, msg);
				//getNegativityPlayer().getTimingPacket().add(packet); // prepare for beeing done after ping things
			}
		}
	}
}
