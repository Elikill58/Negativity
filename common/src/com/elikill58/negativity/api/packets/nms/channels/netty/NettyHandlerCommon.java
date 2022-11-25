package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.api.packets.packet.NPacketUnset;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.logger.LoggerAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class NettyHandlerCommon {

	private static final List<String> sentMessages = new ArrayList<>();

	public static void manageError(ChannelHandlerContext ctx, Throwable cause, String source) {
		if (cause.getMessage().toLowerCase(Locale.ENGLISH).contains("connection reset by ")
				|| cause.getLocalizedMessage().toLowerCase(Locale.ENGLISH).contains("connection reset by "))
			return;
		String causeName = cause.getClass().getName();
		if (causeName.contains("com.viaversion.viaversion.exception.CancelEncoderException")
				|| causeName.contains("com.viaversion.viaversion.exception.CancelDecoderException"))
			return;
		if (cause instanceof ClosedChannelException)
			return;
		Adapter.getAdapter().getLogger().error("Exception caught when " + source + " packet");
		cause.printStackTrace();
	}

	public static NPacket readPacketFromByteBuf(Player p, Version version, PacketDirection direction,
			ChannelHandlerContext ctx, ByteBuf msg, String comment) throws Exception {
		ByteBuf buf = msg.copy();
		int packetId = new PacketSerializer(buf).readVarInt();
		NPacket packet = version.getOrCreateNamedVersion().getPacket(direction, packetId);
		if (packet == null)
			return null;
		try {
			packet.read(new PacketSerializer(buf), version);
		} catch (IndexOutOfBoundsException e) {
			LoggerAdapter ada = Adapter.getAdapter().getLogger();
			ada.warn("Failed to read packet with ID " + packetId + " ("
					+ (packet instanceof NPacketUnset ? ((NPacketUnset) packet).getPacketTypeCible()
							: packet.getPacketType())
					+ ") to " + p.getName() + " (" + direction.name() + " - " + comment + " - " + version.getName()
					+ ")");
			String key = packetId + "_" + packet.getPacketType() + "_" + e.getMessage();
			if (sentMessages.contains(key))
				return packet;
			sentMessages.add(key);
			ada.warn(e.getMessage());
			e.printStackTrace();
		}
		return packet;
	}
}
