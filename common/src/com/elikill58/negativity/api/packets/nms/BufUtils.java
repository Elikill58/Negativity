package com.elikill58.negativity.api.packets.nms;

import static com.elikill58.negativity.universal.utils.Maths.isOutOfBounds;

import java.util.Arrays;

import io.netty.buffer.ByteBuf;

public class BufUtils {

	/**
	 * Create a copy of the underlying storage from {@code buf} into a byte array.
	 * The copy will start at {@link ByteBuf#readerIndex()} and copy
	 * {@link ByteBuf#readableBytes()} bytes.
	 * 
	 * @param buf Buffer that contains all bytes
	 * @return bytes of buffer
	 */
	public static byte[] getBytes(ByteBuf buf) {
		return getBytes(buf, buf.readerIndex(), buf.readableBytes());
	}

	/**
	 * Create a copy of the underlying storage from {@code buf} into a byte array.
	 * The copy will start at {@code start} and copy {@code length} bytes.
	 * 
	 * @param buf Buffer that contains all bytes
	 * @param start the start index of bytes
	 * @param length the length of bytes
	 * @return bytes of buffer
	 */
	public static byte[] getBytes(ByteBuf buf, int start, int length) {
		return getBytes(buf, start, length, true);
	}

	/**
	 * Return an array of the underlying storage from {@code buf} into a byte array.
	 * The copy will start at {@code start} and copy {@code length} bytes. If
	 * {@code copy} is true a copy will be made of the memory. If {@code copy} is
	 * false the underlying storage will be shared, if possible.
	 * 
	 * @param buf Buffer that contains all bytes
	 * @param start the start index of bytes
	 * @param length the length of bytes
	 * @param copy true if should copy the content from buffer (and don't change the index)
	 * @return bytes of buffer
	 */
	public static byte[] getBytes(ByteBuf buf, int start, int length, boolean copy) {
		int capacity = buf.capacity();
		if (isOutOfBounds(start, length, capacity)) {
			throw new IndexOutOfBoundsException("expected: " + "0 <= start(" + start + ") <= start + length(" + length + ") <= " + "buf.capacity(" + capacity + ')');
		}

		if (buf.hasArray()) {
			int baseOffset = buf.arrayOffset() + start;
			byte[] bytes = buf.array();
			if (copy || baseOffset != 0 || length != bytes.length) {
				return Arrays.copyOfRange(bytes, baseOffset, baseOffset + length);
			} else {
				return bytes;
			}
		}

		byte[] bytes = new byte[length];
		buf.getBytes(start, bytes);
		buf.readerIndex(start); // be sure to go back at begin
		return bytes;
	}
}
