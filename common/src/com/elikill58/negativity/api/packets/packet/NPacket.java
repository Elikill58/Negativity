package com.elikill58.negativity.api.packets.packet;

import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.universal.Version;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public interface NPacket {

	/**
	 * Read packet from given serializer.
	 * 
	 * @param serializer the content of packet
	 */
	public void read(PacketSerializer serializer, Version version);
	
	/**
	 * Write packet to given serializer
	 * 
	 * @param serializer the buffer that will receive write
	 */
	public default void write(PacketSerializer serializer, Version version) {
		throw new UnsupportedOperationException("Can't write packet " + getPacketType() + " (" + getPacketName() + "). Not implemented.");
	}
	
	/**
	 * Create ByteBuf of this packet with all necessary data
	 * 
	 * @return new filled buffer
	 */
	public default ByteBuf create(Version version) {
		PacketSerializer serializer = new PacketSerializer(Unpooled.buffer());
		write(serializer, version);
		return serializer.getBuf();
	}
	
	/**
	 * Get type of packet
	 * 
	 * @return packet type
	 */
	public PacketType getPacketType();

	public default String getPacketName() {
		return getPacketType().getPacketName();
	}
}
