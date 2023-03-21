package com.elikill58.negativity.api.packets.nms.channels.netty;

import java.io.IOException;
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
		if (cause.getMessage().toLowerCase(Locale.ENGLISH).contains("connection reset by ") || cause.getLocalizedMessage().toLowerCase(Locale.ENGLISH).contains("connection reset by "))
			return;
		String causeName = cause.getClass().getName();
		if (causeName.contains("com.viaversion.viaversion.exception.CancelEncoderException") || causeName.contains("com.viaversion.viaversion.exception.CancelDecoderException"))
			return;
		if (cause instanceof ClosedChannelException)
			return;
		if (cause instanceof IOException) // TODO handle better the io things
			return; // closing channel
		ctx.fireExceptionCaught(cause);
		// Adapter.getAdapter().getLogger().error("Exception caught when " + source + "
		// packet");
		// cause.printStackTrace();
	}

	/**
	 * Read packet from given byte buf
	 * 
	 * @param p         the concerned player
	 * @param version   the version of player
	 * @param direction the direction of the received buffer
	 * @param buf       used buffer
	 * @param comment   the comment like the name of the channel
	 * @return a new packet (read) or null if can't find something
	 */
	public static NPacket readPacketFromByteBuf(Player p, Version version, PacketDirection direction, ByteBuf buf, String comment) {
		if (!buf.isReadable())
			return null;
		PacketSerializer serializer = new PacketSerializer(p, buf);
		int packetId = serializer.readVarInt();
		NPacket packet = version.getNamedVersion().getPacket(direction, packetId);
		if (packet == null)
			return null;
		try {
			packet.read(serializer, version);
		} catch (IndexOutOfBoundsException e) {
			LoggerAdapter ada = Adapter.getAdapter().getLogger();
			String key = packetId + "_" + packet.getPacketType() + "_" + e.getMessage();
			if (!sentMessages.contains(key)) {
				sentMessages.add(key);
				ada.printError("Failed to read packet with ID " + packetId + " (" + (packet instanceof NPacketUnset ? ((NPacketUnset) packet).getPacketTypeCible() : packet.getPacketType())
						+ ") to " + p.getName() + " (" + direction.name() + " - " + comment + " - " + version.getName() + ")", e);
			}
			return null; // try to don't return partial packet
		}
		return packet;
	}
}
