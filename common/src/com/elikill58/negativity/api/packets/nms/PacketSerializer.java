package com.elikill58.negativity.api.packets.nms;

import java.io.DataInput;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.block.BlockPosition;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.logger.Debug;
import com.github.steveice10.opennbt.NBTIO;
import com.github.steveice10.opennbt.tag.TagRegistry;
import com.github.steveice10.opennbt.tag.builtin.CompoundTag;
import com.github.steveice10.opennbt.tag.limiter.TagLimiter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.handler.codec.DecoderException;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class PacketSerializer extends UnpooledHeapByteBuf {

	private static final int CONTINUE_BIT = 0x80;
	private static final int VALUE_BITS = 0x7F;
	
	static {
		// Sanitize result: this is only to pre-load some class of OpenNBT
		TagRegistry.getClassFor(0);
		new Object2IntOpenHashMap<Object>();
	}

	private final Player player;

	/**
	 * Create new packet serializer.<br>
	 * Prefer use constructor with direct content
	 * 
	 * @param buf actual buffer
	 */
	public PacketSerializer(ByteBuf buf) {
		this(null, buf);
	}

	/**
	 * Create new packet serializer
	 * 
	 * @param p concerned player
	 */
	public PacketSerializer(@Nullable Player p) {
		this(p, new byte[0]);
	}

	/**
	 * Create new packet serializer for the given player
	 * 
	 * @param p   the concerned player
	 * @param buf the used buffer
	 */
	public PacketSerializer(@Nullable Player p, ByteBuf buf) {
		this(p, BufUtils.getBytes(buf));
	}

	public PacketSerializer(@Nullable Player p, byte[] array) {
		this(p, array, Integer.MAX_VALUE);
	}

	public PacketSerializer(@Nullable Player p, byte[] array, int maxLength) {
		super(UnpooledByteBufAllocator.DEFAULT, array, maxLength);
		this.player = p;
	}

	public @Nullable Player getPlayer() {
		return player;
	}

	public int readVarInt() {
		int result = 0;
		for (int shift = 0;; shift += 7) {
			byte b = readByte();
			result |= (b & VALUE_BITS) << shift;
			if (b >= 0) {
				return result;
			}
		}
	}

	public int readInt(int byteAmount) {
		int result = 0;
		for (int i = 0; i < byteAmount; i++) {
			result |= (readByte() & VALUE_BITS) << i;
		}
		return result;
	}

	public <T extends Enum<T>> T getEnum(Class<T> oclass) {
		try {
			return (T) ((Enum[]) oclass.getEnumConstants())[readVarInt()];
		} catch(ArrayIndexOutOfBoundsException e) {
			Adapter.getAdapter().debug(Debug.GENERAL, "ArrayIndexOutOfBoundsException for " + e.getMessage() + ": " + e.getStackTrace()[0]);
		}
		return null;
	}

	public void writeEnum(Enum<?> oenum) {
		writeVarInt(oenum.ordinal());
	}

	public long readVarLong() {
		long result = 0;
		for (int shift = 0; shift < 56; shift += 7) {
			byte b = readByte();
			result |= (b & VALUE_BITS) << shift;
			if (b >= 0) {
				return result;
			}
		}
		return result | (readByte() & 0xffL) << 56;
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

	public byte[] readAvailableBytes() {
		byte[] b = new byte[readableBytes()];
		readBytes(b);
		return b;
	}

	public Instant readInstant() {
		return Instant.ofEpochMilli(readLong());
	}

	public String readString() {
		int j = readVarInt();
		if (j < 0)
			throw new DecoderException("The received encoded string buffer length is less than zero! (" + j + ")");
		byte[] bytes = new byte[j];
		for (int i = 0; i < j; i++)
			bytes[i] = readByte();
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public String readString(int size) {
		int j = readVarInt();
		if (j < 0)
			throw new DecoderException("The received encoded string buffer length is less than zero! (" + j + ")");
		if (j > size * 4)
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + (size * 4) + ")");
		byte[] bytes = new byte[j];
		for (int i = 0; i < j; i++)
			bytes[i] = readByte();
		String s = new String(bytes, StandardCharsets.UTF_8);
		if (s.length() > size)
			throw new DecoderException("Received string length is longer than maximum allowed (" + j + " > " + size + ")");
		return s;
	}

	public void writeString(String s) {
		byte[] bytes = s.getBytes();
		writeVarInt(bytes.length);
		writeBytes(bytes);
	}

	/**
	 * Read NBT tag
	 * 
	 * @return compound tag or null
	 */
	public CompoundTag readNBTTag() {
		try {
			int readerIndex = readerIndex();
			byte b = readByte();
			if (b == 0)
				return null;
			readerIndex(readerIndex);
			return NBTIO.readTag((DataInput) new ByteBufInputStream(this), TagLimiter.create(2097152, 512));
		} catch (Exception e) {}
		return null;
	}
	
	@Deprecated
	public ItemStack readItemStack() {
		return readItemStack(Version.V1_8);
	}

	/**
	 * Read item stack according to given version<br>
	 * WARN: This will always return null, it just pass through bytes
	 * 
	 * @param version the version
	 * @return null
	 */
	public ItemStack readItemStack(Version version) {
		if(version.isNewerOrEquals(Version.V1_15)) {
			if(!readBoolean())
				return null;
		} else {
			if (readShort() < 0)
				return null;
		}
		readByte(); // amount
		readShort(); // data
		readNBTTag(); // just read things but not take in count yet
		return null;
	}

	public BlockPosition readChunkSectionPosition() {
		long l = readLong();
		long sectionX = l >> 42;
		long sectionY = l << 44 >> 44;
		long sectionZ = l << 22 >> 42;
		return new BlockPosition((int) sectionX, (int) sectionY, (int) sectionZ);
	}

	public BlockPosition readBlockPosition(Version version) {
		return version.isNewerOrEquals(Version.V1_17) ? readBlockPositionNew() : readBlockPositionOld();
	}

	private BlockPosition readBlockPositionNew() {
		long value = readLong();
		int x = (int) (value >> 38);
		int y = (int) (value << 52 >> 52);
		int z = (int) (value << 26 >> 38);
		return new BlockPosition(x, y, z);
	}

	private BlockPosition readBlockPositionOld() {
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

	public long[] readLongArray() {
		return readLongArray(Integer.MAX_VALUE);
	}

	public long[] readLongArray(int maxSize) {
		int length = readVarInt();
		if (length <= 0)
			return new long[0];
		if (length > maxSize)
			throw new RuntimeException("VarInt too high for long array reading.");
		long[] longs = new long[length];
		for (int i = 0; i < length; i++) {
			longs[i] = readLong();
		}
		return longs;
	}

	public byte[] readByteArray() {
		return readByteArray(Integer.MAX_VALUE);
	}

	public byte[] readByteArray(int maxSize) {
		int length = readVarInt();
		if (length <= 0)
			return new byte[0];
		if (length > maxSize)
			throw new RuntimeException("VarInt too high for byte array reading.");
		byte[] bytes = new byte[length];
		readBytes(bytes);
		return bytes;
	}

	public void writeByteArray(byte[] bytes) {
		writeVarInt(bytes.length);
		writeBytes(bytes);
	}
}
