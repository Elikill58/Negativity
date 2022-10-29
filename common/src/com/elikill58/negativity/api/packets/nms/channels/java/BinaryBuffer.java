package com.elikill58.negativity.api.packets.nms.channels.java;

import java.nio.ByteBuffer;

public final class BinaryBuffer {
	private ByteBuffer nioBuffer; // To become a `MemorySegment` once released

	private final int capacity;
	private int readerOffset, writerOffset;

	private BinaryBuffer(ByteBuffer buffer) {
		this.nioBuffer = buffer;
		this.capacity = buffer.capacity();
	}

	public static BinaryBuffer wrap(ByteBuffer buffer) {
		assert buffer.isDirect();
		return new BinaryBuffer(buffer);
	}

	public int readVarInt() {
		int value = 0;
		for (int i = 0; i < 5; i++) {
			final int offset = readerOffset + i;
			final byte k = nioBuffer.get(offset);
			value |= (k & 0x7F) << i * 7;
			if ((k & 0x80) != 128) {
				this.readerOffset = offset + 1;
				return value;
			}
		}
		throw new RuntimeException("VarInt is too big");
	}

	public Marker mark() {
		return new Marker(readerOffset, writerOffset);
	}

	public void reset(int readerOffset, int writerOffset) {
		this.readerOffset = readerOffset;
		this.writerOffset = writerOffset;
	}

	public void reset(Marker marker) {
		reset(marker.getReaderOffset(), marker.getWriterOffset());
	}

	public boolean canRead(int size) {
		return readerOffset + size <= writerOffset;
	}

	public int capacity() {
		return capacity;
	}

	public int readerOffset() {
		return readerOffset;
	}

	public void readerOffset(int offset) {
		this.readerOffset = offset;
	}

	public int writerOffset() {
		return writerOffset;
	}

	public int readableBytes() {
		return writerOffset - readerOffset;
	}

	public byte[] readBytes(int length) {
		byte[] bytes = new byte[length];
		this.nioBuffer.get(readerOffset);
		this.readerOffset += length;
		return bytes;
	}

	public BinaryBuffer clear() {
		this.readerOffset = 0;
		this.writerOffset = 0;
		this.nioBuffer.limit(capacity);
		return this;
	}

	public ByteBuffer asByteBuffer(int reader) {
		nioBuffer.position(reader); // change start pos
		return nioBuffer.slice();
	}

	public ByteBuffer asByteBuffer() {
		return nioBuffer;
	}

	@Override
	public String toString() {
		return "BinaryBuffer{" + "readerOffset=" + readerOffset + ", writerOffset=" + writerOffset + ", capacity="
				+ capacity + '}';
	}

	public final class Marker {

		public int readerOffset, writerOffset;

		Marker(int readerOffset, int writerOffset) {
			this.readerOffset = readerOffset;
			this.writerOffset = writerOffset;
		}

		public int getReaderOffset() {
			return readerOffset;
		}

		public int getWriterOffset() {
			return writerOffset;
		}
	}
}