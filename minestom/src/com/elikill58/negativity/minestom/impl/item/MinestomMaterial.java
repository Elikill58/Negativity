package com.elikill58.negativity.minestom.impl.item;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.item.Material;

import net.minestom.server.instance.block.Block;

/**
 * Minestom material backed by an item ({@link net.minestom.server.item.Material}), a block ({@link Block}), or both.
 * <p>
 * Some materials only exist as blocks (water, lava, fire, bubble_column...), so they cannot be found in the
 * item registry. The id is always the registry key ({@code minecraft:water}) so that an item-backed and a
 * block-backed material of the same thing are equal.
 */
public class MinestomMaterial extends Material {

	private final net.minestom.server.item.@Nullable Material item;
	private final @Nullable Block block;

	public MinestomMaterial(net.minestom.server.item.Material item) {
		this.item = item;
		this.block = item.block();
	}

	public MinestomMaterial(Block block) {
		this.block = block;
		this.item = block.material();
	}

	/**
	 * @return the item form of the given material, or null if it has none (block-only material or not a Minestom material)
	 */
	public static net.minestom.server.item.@Nullable Material itemOf(Material type) {
		return type instanceof MinestomMaterial mm ? mm.item : null;
	}

	/**
	 * @return the block form of the given material, or null if it has none (item-only material or not a Minestom material)
	 */
	public static @Nullable Block blockOf(Material type) {
		return type instanceof MinestomMaterial mm ? mm.block : null;
	}

	@Override
	public boolean isSolid() {
		// same semantic as before for item-backed materials (any block item counts as solid); real solidity otherwise
		return item != null ? block != null : block.solid();
	}

	@Override
	public String getId() {
		return (item != null ? item.key() : block.key()).asString();
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public Object getDefault() {
		return item != null ? item : block;
	}
}
