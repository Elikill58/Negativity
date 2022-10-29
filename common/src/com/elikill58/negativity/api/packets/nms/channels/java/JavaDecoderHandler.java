package com.elikill58.negativity.api.packets.nms.channels.java;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Consumer;
import java.util.zip.DataFormatException;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.packets.PacketDirection;
import com.elikill58.negativity.api.packets.nms.PacketSerializer;
import com.elikill58.negativity.api.packets.nms.channels.java.BinaryBuffer.Marker;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.universal.Adapter;

import io.netty.buffer.Unpooled;

public class JavaDecoderHandler implements Consumer<SelectionKey> {

	private final Player p;
	private boolean compressed = false;
	
	public JavaDecoderHandler(Player p) {
		this.p = p;
	}
	
	public void read(BinaryBuffer readBuffer, boolean compressed) throws DataFormatException {
        ByteBuffer pool = ByteBuffer.allocateDirect(2_097_151);
        while (readBuffer.readableBytes() > 0) {
            Marker beginMark = readBuffer.mark();
            try {
                // Ensure that the buffer contains the full packet (or wait for next socket read)
                final int packetLength = readBuffer.readVarInt();
                final int readerStart = readBuffer.readerOffset();
                if (!readBuffer.canRead(packetLength)) {
                    // Integrity fail
                    throw new BufferUnderflowException();
                }
                // Read packet https://wiki.vg/Protocol#Packet_format
                BinaryBuffer content = readBuffer;
                if (compressed) {
                    final int dataLength = readBuffer.readVarInt();
                    final int payloadLength = packetLength - (readBuffer.readerOffset() - readerStart);
                    if (payloadLength < 0) {
                        throw new DataFormatException("Negative payload length " + payloadLength);
                    }
                    if (dataLength == 0) {
                        // Data is too small to be compressed, payload is following
                    } else {
                        // Decompress to content buffer
                        content = BinaryBuffer.wrap(pool);
                    }
                }
                // Slice packet
                ByteBuffer payload = content.asByteBuffer(content.readerOffset());
                final int packetId = new PacketSerializer(Unpooled.wrappedBuffer(payload)).readVarInt();
    			NPacket packet = Adapter.getAdapter().getVersionAdapter().getVersion().getPacket(PacketDirection.CLIENT_TO_SERVER, packetId);
    			if(packet == null)
    				return;
    			packet.read(new PacketSerializer(Unpooled.wrappedBuffer(payload)));
    			PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
    			EventManager.callEvent(event);
    			
                // Position buffer to read the next packet
                readBuffer.readerOffset(readerStart + packetLength);
            } catch (BufferUnderflowException e) {
                readBuffer.reset(beginMark);
                break;
            }
        }
	}

	@Override
	public void accept(SelectionKey key) {
		final SocketChannel channel = (SocketChannel) key.channel();
		if (!channel.isOpen())
			return;
		if (!key.isReadable())
			return;
		try {
			BinaryBuffer readBuffer = BinaryBuffer.wrap(ByteBuffer.allocateDirect(2_097_151));
	        ByteBuffer pool = ByteBuffer.allocateDirect(2_097_151);
	        while (readBuffer.readableBytes() > 0) {
	            Marker beginMark = readBuffer.mark();
	            try {
	                // Ensure that the buffer contains the full packet (or wait for next socket read)
	                final int packetLength = readBuffer.readVarInt();
	                final int readerStart = readBuffer.readerOffset();
	                if (!readBuffer.canRead(packetLength)) {
	                    // Integrity fail
	                    throw new BufferUnderflowException();
	                }
	                // Read packet https://wiki.vg/Protocol#Packet_format
	                BinaryBuffer content = readBuffer;
	                if (compressed) {
	                    final int dataLength = readBuffer.readVarInt();
	                    final int payloadLength = packetLength - (readBuffer.readerOffset() - readerStart);
	                    if (payloadLength < 0) {
	                        throw new DataFormatException("Negative payload length " + payloadLength);
	                    }
	                    if (dataLength == 0) {
	                        // Data is too small to be compressed, payload is following
	                    } else {
	                        // Decompress to content buffer
	                        content = BinaryBuffer.wrap(pool);
	                    }
	                }
	                // Slice packet
	                ByteBuffer payload = content.asByteBuffer(content.readerOffset());
	                final int packetId = new PacketSerializer(Unpooled.wrappedBuffer(payload)).readVarInt();
	    			NPacket packet = Adapter.getAdapter().getVersionAdapter().getVersion().getPacket(PacketDirection.CLIENT_TO_SERVER, packetId);
	    			packet.read(new PacketSerializer(Unpooled.wrappedBuffer(payload)));
	    			PacketReceiveEvent event = new PacketReceiveEvent(packet, p);
	    			EventManager.callEvent(event);
	    			
	                // Position buffer to read the next packet
	                readBuffer.readerOffset(readerStart + packetLength);
	            } catch (BufferUnderflowException e) {
	                readBuffer.reset(beginMark);
	                break;
	            }
	        }
		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
