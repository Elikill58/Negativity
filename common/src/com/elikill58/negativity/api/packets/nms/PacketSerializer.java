package com.elikill58.negativity.api.packets.nms;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
import io.netty.handler.codec.DecoderException;

public class PacketSerializer {

	private static final int CONTINUE_BIT = 0x80;
	private static final int VALUE_BITS = 0x7F;
	private static final int MAX_BYTES = 5;

	private final ByteBuf buf;

	public PacketSerializer(ByteBuf buf) {
		this.buf = buf;
	}

	public ByteBuf getBuf() {
		return buf;
	}

	public int readVarInt() {
		int value = 0;
		int bytes = 0;
		byte in;
		do {
			in = buf.readByte();
			value |= (in & VALUE_BITS) << (bytes++ * 7);
			if (bytes > MAX_BYTES) {
				throw new RuntimeException("VarInt too big");
			}

		} while ((in & CONTINUE_BIT) == CONTINUE_BIT);
		return value;
	}

	public <T extends Enum<T>> T getEnum(Class<T> oclass) {
		return (T) ((Enum[]) oclass.getEnumConstants())[readVarInt()];
	}

	public void writeEnum(Enum<?> oenum) {
		writeVarInt(oenum.ordinal());
	}

	public long readVarLong() {
		byte b0;
		long i = 0L;
		int j = 0;
		do {
			b0 = readByte();
			i |= (b0 & Byte.MAX_VALUE) << j++ * 7;
			if (j > 10)
				throw new RuntimeException("VarLong too big");
		} while ((b0 & 0x80) == 128);
		return i;
	}

	public void write(UUID uuid) {
		writeLong(uuid.getMostSignificantBits());
		writeLong(uuid.getLeastSignificantBits());
	}

	public UUID readUUID() {
		return new UUID(readLong(), readLong());
	}

	public void writeVarInt(int i) {
		while ((i & 0xFFFFFF80) != 0) {
			writeByte(i & VALUE_BITS | CONTINUE_BIT);
			i >>>= 7;
		}
		writeByte(i);
	}

	public void writeVarLong(long i) {
		while ((i & 0xFFFFFFFFFFFFFF80L) != 0L) {
			writeByte((int) (i & VALUE_BITS) | CONTINUE_BIT);
			i >>>= 7L;
		}
		writeByte((int) i);
	}

	public int capacity() {
		return this.buf.capacity();
	}

	public ByteBuf capacity(int i) {
		return this.buf.capacity(i);
	}

	public int maxCapacity() {
		return this.buf.maxCapacity();
	}

	public ByteBufAllocator alloc() {
		return this.buf.alloc();
	}

	public ByteOrder order() {
		return this.buf.order();
	}

	public ByteBuf order(ByteOrder byteorder) {
		return this.buf.order(byteorder);
	}

	public ByteBuf unwrap() {
		return this.buf.unwrap();
	}

	public boolean isDirect() {
		return this.buf.isDirect();
	}

	public int readerIndex() {
		return this.buf.readerIndex();
	}

	public ByteBuf readerIndex(int i) {
		return this.buf.readerIndex(i);
	}

	public boolean isReadable() {
		return this.buf.isReadable();
	}

	public boolean isReadable(int i) {
		return this.buf.isReadable(i);
	}

	public boolean isWritable() {
		return this.buf.isWritable();
	}

	public boolean isWritable(int i) {
		return this.buf.isWritable(i);
	}

	public ByteBuf clear() {
		return this.buf.clear();
	}

	public boolean readBoolean() {
		return this.buf.readBoolean();
	}

	public byte readByte() {
		return this.buf.readByte();
	}

	public short readUnsignedByte() {
		return this.buf.readUnsignedByte();
	}

	public short readShort() {
		return this.buf.readShort();
	}

	public int readUnsignedShort() {
		return this.buf.readUnsignedShort();
	}

	public int readMedium() {
		return this.buf.readMedium();
	}

	public int readUnsignedMedium() {
		return this.buf.readUnsignedMedium();
	}

	public int readInt() {
		return this.buf.readInt();
	}

	public long readUnsignedInt() {
		return this.buf.readUnsignedInt();
	}

	public long readLong() {
		return this.buf.readLong();
	}

	public char readChar() {
		return this.buf.readChar();
	}

	public float readFloat() {
		return this.buf.readFloat();
	}

	public double readDouble() {
		return this.buf.readDouble();
	}

	public ByteBuf readBytes(int i) {
		return this.buf.readBytes(i);
	}

	public ByteBuf readSlice(int i) {
		return this.buf.readSlice(i);
	}

	public ByteBuf readBytes(ByteBuf bytebuf) {
		return this.buf.readBytes(bytebuf);
	}

	public ByteBuf readBytes(ByteBuf bytebuf, int i) {
		return this.buf.readBytes(bytebuf, i);
	}

	public ByteBuf readBytes(ByteBuf bytebuf, int i, int j) {
		return this.buf.readBytes(bytebuf, i, j);
	}

	public ByteBuf readBytes(byte[] abyte) {
		return this.buf.readBytes(abyte);
	}

	public ByteBuf readBytes(byte[] abyte, int i, int j) {
		return this.buf.readBytes(abyte, i, j);
	}

	public ByteBuf readBytes(ByteBuffer bytebuffer) {
		return this.buf.readBytes(bytebuffer);
	}

	public ByteBuf readBytes(OutputStream outputstream, int i) throws IOException {
		return this.buf.readBytes(outputstream, i);
	}

	public int readBytes(GatheringByteChannel gatheringbytechannel, int i) throws IOException {
		return this.buf.readBytes(gatheringbytechannel, i);
	}

	public ByteBuf skipBytes(int i) {
		return this.buf.skipBytes(i);
	}

	public ByteBuf writeBoolean(boolean flag) {
		return this.buf.writeBoolean(flag);
	}

	public ByteBuf writeByte(int i) {
		return this.buf.writeByte(i);
	}

	public ByteBuf writeShort(int i) {
		return this.buf.writeShort(i);
	}

	public ByteBuf writeMedium(int i) {
		return this.buf.writeMedium(i);
	}

	public ByteBuf writeInt(int i) {
		return this.buf.writeInt(i);
	}

	public ByteBuf writeLong(long i) {
		return this.buf.writeLong(i);
	}

	public ByteBuf writeChar(int i) {
		return this.buf.writeChar(i);
	}

	public ByteBuf writeFloat(float f) {
		return this.buf.writeFloat(f);
	}

	public ByteBuf writeDouble(double d0) {
		return this.buf.writeDouble(d0);
	}

	public ByteBuf writeBytes(ByteBuf bytebuf) {
		return this.buf.writeBytes(bytebuf);
	}

	public ByteBuf writeBytes(ByteBuf bytebuf, int i) {
		return this.buf.writeBytes(bytebuf, i);
	}

	public ByteBuf writeBytes(ByteBuf bytebuf, int i, int j) {
		return this.buf.writeBytes(bytebuf, i, j);
	}

	public ByteBuf writeBytes(byte[] abyte) {
		return this.buf.writeBytes(abyte);
	}

	public ByteBuf writeBytes(byte[] abyte, int i, int j) {
		return this.buf.writeBytes(abyte, i, j);
	}

	public ByteBuf writeBytes(ByteBuffer bytebuffer) {
		return this.buf.writeBytes(bytebuffer);
	}

	public int writeBytes(InputStream inputstream, int i) throws IOException {
		return this.buf.writeBytes(inputstream, i);
	}

	public int writeBytes(ScatteringByteChannel scatteringbytechannel, int i) throws IOException {
		return this.buf.writeBytes(scatteringbytechannel, i);
	}

	public ByteBuf writeZero(int i) {
		return this.buf.writeZero(i);
	}

	public int indexOf(int i, int j, byte b0) {
		return this.buf.indexOf(i, j, b0);
	}

	public int bytesBefore(byte b0) {
		return this.buf.bytesBefore(b0);
	}

	public int bytesBefore(int i, byte b0) {
		return this.buf.bytesBefore(i, b0);
	}

	public int bytesBefore(int i, int j, byte b0) {
		return this.buf.bytesBefore(i, j, b0);
	}

	public int forEachByte(ByteBufProcessor bytebufprocessor) {
		return this.buf.forEachByte(bytebufprocessor);
	}

	public ByteBuf copy() {
		return this.buf.copy();
	}

	public ByteBuf copy(int i, int j) {
		return this.buf.copy(i, j);
	}

	public ByteBuf slice() {
		return this.buf.slice();
	}

	public ByteBuf slice(int i, int j) {
		return this.buf.slice(i, j);
	}

	public ByteBuf duplicate() {
		return this.buf.duplicate();
	}

	public int nioBufferCount() {
		return this.buf.nioBufferCount();
	}

	public ByteBuffer nioBuffer() {
		return this.buf.nioBuffer();
	}

	public boolean hasArray() {
		return this.buf.hasArray();
	}

	public byte[] array() {
		return this.buf.array();
	}

	public int arrayOffset() {
		return this.buf.arrayOffset();
	}

	public boolean hasMemoryAddress() {
		return this.buf.hasMemoryAddress();
	}

	public long memoryAddress() {
		return this.buf.memoryAddress();
	}

	public String toString(Charset charset) {
		return this.buf.toString(charset);
	}

	public String toString(int i, int j, Charset charset) {
		return this.buf.toString(i, j, charset);
	}

	public int hashCode() {
		return this.buf.hashCode();
	}

	public boolean equals(Object object) {
		return this.buf.equals(object);
	}

	public int compareTo(ByteBuf bytebuf) {
		return this.buf.compareTo(bytebuf);
	}

	public String toString() {
		return this.buf.toString();
	}

	public ByteBuf retain(int i) {
		return this.buf.retain(i);
	}

	public ByteBuf retain() {
		return this.buf.retain();
	}

	public int refCnt() {
		return this.buf.refCnt();
	}

	public boolean release() {
		return this.buf.release();
	}

	public boolean release(int i) {
		return this.buf.release(i);
	}

	public String readString(int size) {
		int j = readVarInt();
		if (j > size * 4)
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j
					+ " > " + (size * 4) + ")");
		if (j < 0)
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		String s = new String(readBytes(j).array(), StandardCharsets.UTF_8);
		if (s.length() > size)
			throw new DecoderException(
					"Received string length is longer than maximum allowed (" + j + " > " + size + ")");
		return s;
	}

	public Object readNBTTag() {
		int i = readerIndex();
		byte b0 = readByte();
		if (b0 == 0)
			return null;
		readerIndex(i);
		return null;
	}

	@SuppressWarnings("unused")
	public ItemStack readItemStack() {
		ItemStack itemstack = null;
		short itemId = readShort();
		if (itemId >= 0) {
			byte amount = readByte();
			short data = readShort();
			readNBTTag();
			// just read things but not take in count yet
			/*
			 * itemstack = new ItemStack(Item.getById(itemId), amount, data);
			 * itemstack.setTag(readNBTTag());
			 */
		}
		return itemstack;
	}

	public BlockPosition readBlockPosition() {
		long value = readLong();
	    int x = (int) (value >> 38);
	    int y = (int) (value << 26 >> 52);
	    int z = (int) (value << 38 >> 38);
		return new BlockPosition(x, y, z);
	}

	public BlockPosition readBlockPositionShort() {
		long value = readShort();
	    int x = (int) (value >> 38);
	    int y = (int) (value << 26 >> 52);
	    int z = (int) (value << 38 >> 38);
		return new BlockPosition(x, y, z);
	}
	
	public Vector readVector() {
		return new Vector(readFloat(), readFloat(), readFloat());
	}
	
	public Vector readShortVector() {
		return new Vector(readShort(), readShort(), readShort());
	}
}
