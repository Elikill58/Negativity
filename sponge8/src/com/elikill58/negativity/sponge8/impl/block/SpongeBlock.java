package com.elikill58.negativity.sponge8.impl.block;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.ServerLocation;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge8.impl.location.SpongeLocation;
import com.elikill58.negativity.sponge8.utils.Utils;

public class SpongeBlock extends Block {
	
	private @Nullable Material cachedMaterial;
	private final BlockSnapshot block;
	
	public SpongeBlock(BlockSnapshot block) {
		this.block = block;
	}
	
	@Override
	public Material getType() {
		if (this.cachedMaterial == null) {
			ResourceKey key = Utils.getKey(block.getState().getType());
			this.cachedMaterial = ItemRegistrar.getInstance().get(key.asString());
		}
		return this.cachedMaterial;
	}
	
	@Override
	public int getX() {
		return block.getPosition().getX();
	}
	
	@Override
	public int getY() {
		return block.getPosition().getY();
	}
	
	@Override
	public int getZ() {
		return block.getPosition().getZ();
	}
	
	@Override
	public Block getRelative(BlockFace blockFace) {
		return block.getLocation()
			.map(loc -> new SpongeBlock(loc.relativeTo(getDirection(blockFace)).createSnapshot()))
			.orElse(this);
	}
	
	private Direction getDirection(BlockFace bf) {
		switch (bf) {
		case DOWN:
			return Direction.DOWN;
		case EAST:
			return Direction.EAST;
		case EAST_NORTH_EAST:
			return Direction.EAST_NORTHEAST;
		case EAST_SOUTH_EAST:
			return Direction.EAST_SOUTHEAST;
		case NORTH:
			return Direction.NORTH;
		case NORTH_EAST:
			return Direction.NORTHEAST;
		case NORTH_NORTH_EAST:
			return Direction.NORTH_NORTHEAST;
		case NORTH_NORTH_WEST:
			return Direction.NORTH_NORTHWEST;
		case NORTH_WEST:
			return Direction.NORTHWEST;
		case SELF:
			return Direction.NONE;
		case SOUTH:
			return Direction.SOUTH;
		case SOUTH_EAST:
			return Direction.SOUTHEAST;
		case SOUTH_SOUTH_EAST:
			return Direction.SOUTH_SOUTHEAST;
		case SOUTH_SOUTH_WEST:
			return Direction.SOUTH_SOUTHWEST;
		case SOUTH_WEST:
			return Direction.SOUTHWEST;
		case UP:
			return Direction.UP;
		case WEST:
			return Direction.WEST;
		case WEST_NORTH_WEST:
			return Direction.WEST_NORTHWEST;
		case WEST_SOUTH_WEST:
			return Direction.WEST_SOUTHWEST;
		default:
			return Direction.NONE;
		}
	}
	
	@Override
	public Location getLocation() {
		return new SpongeLocation(block.getLocation().get());
	}
	
	@Override
	public boolean isLiquid() {
		String name = getType().getId().toLowerCase(Locale.ROOT);
		return name.contains("water") || name.contains("lava");
	}
	
	@Override
	public void setType(Material type) {
		ServerLocation loc = block.getLocation().orElse(null);
		if (loc != null) {
			ItemType item = (ItemType) type.getDefault();
			item.getBlock().ifPresent(loc::setBlockType);
		}
	}
	
	@Override
	public boolean isWaterLogged() {
		return block.require(Keys.IS_WATERLOGGED);
	}
	
	@Override
	public Object getDefault() {
		return block;
	}
	
}
