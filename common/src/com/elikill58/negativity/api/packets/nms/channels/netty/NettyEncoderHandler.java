package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketSendEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.NamedVersion;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.multiVersion.PlayerVersionManager;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

public class NettyEncoderHandler extends MessageToMessageEncoder<ByteBuf> {

	private final Player p;
	private final PacketDirection direction;
	private final Version v;
	private final NamedVersion version;
	
	public NettyEncoderHandler(Player p, PacketDirection direction) {
		this.p = p;
		this.direction = direction;
		this.version = (v = PlayerVersionManager.getPlayerVersion(p)).getOrCreateNamedVersion();
	}
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		if(cause.getMessage().toLowerCase(Locale.ENGLISH).contains("connection reset by ")
				|| cause.getLocalizedMessage().toLowerCase(Locale.ENGLISH).contains("connection reset by "))
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
		NPacket packet = version.getPacket(direction, packetId);
		if(packet == null) {
			out.add(msg.retain());
			return;
		}
		try {
			packet.read(new PacketSerializer(buf), v);
		} catch (IndexOutOfBoundsException e) {
			Adapter.getAdapter().getLogger().warn("Failed to read packet with ID " + packetId + " for player " + p.getName() + " (dir: " + direction.name() + " - Encode)");
			out.add(msg.retain());
			return;
		}
		PacketSendEvent event = new PacketSendEvent(packet, p);
		EventManager.callEvent(event);
		if(!event.isCancelled())
			out.add(msg.retain());
	}
}
