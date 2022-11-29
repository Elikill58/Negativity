package com.elikill58.negativity.api.block.chunks;

import org.checkerframework.checker.nullness.qual.Nullable;

import io.netty.buffer.ByteBuf;

public class ChunkSectionLight {

	private static final int LIGHT_LENGTH = 16 * 16 * 16 / 2; // Dimensions / 2 (nibble bit count)
	private byte[] blockLight;
	private byte[] skyLight;

	public ChunkSectionLight() {
		// Block light is always written
		this.blockLight = new byte[ChunkSection.SIZE];
	}

	public void setBlockLight(byte[] data) {
		if (data.length != LIGHT_LENGTH)
			throw new IllegalArgumentException("Data length != " + LIGHT_LENGTH);
		this.blockLight = data;
	}

	public void setSkyLight(byte[] data) {
		if (data.length != LIGHT_LENGTH)
			throw new IllegalArgumentException("Data length != " + LIGHT_LENGTH);
		this.skyLight = data;
	}

	public byte[] getBlockLight() {
		return blockLight;
	}

	public @Nullable byte[] getBlockLightNibbleArray() {
		return blockLight;
	}

	public byte[] getSkyLight() {
		return skyLight;
	}

	public @Nullable byte[] getSkyLightNibbleArray() {
		return skyLight;
	}

	public void readBlockLight(ByteBuf input) {
		if (this.blockLight == null) {
			this.blockLight = new byte[LIGHT_LENGTH * 2];
		}
		input.readBytes(this.blockLight);
	}

	public void readSkyLight(ByteBuf input) {
		if (this.skyLight == null) {
			this.skyLight = new byte[LIGHT_LENGTH * 2];
		}
		input.readBytes(this.skyLight);
	}

	public void writeBlockLight(ByteBuf output) {
		output.writeBytes(blockLight);
	}

	public void writeSkyLight(ByteBuf output) {
		output.writeBytes(skyLight);
	}

	public boolean hasSkyLight() {
		return skyLight != null;
	}

	public boolean hasBlockLight() {
		return blockLight != null;
	}
}
