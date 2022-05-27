package com.elikill58.negativity.sponge7.impl.block;

import java.util.Locale;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.sponge7.impl.location.SpongeLocation;

public class SpongeBlock extends Block {

	private final BlockSnapshot block;
	
	public SpongeBlock(BlockSnapshot block) {
		this.block = block;
	}

	@Override
	public Material getType() {
		return ItemRegistrar.getInstance().get(block.getState().getId(), block.getState().getName());
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
		Optional<org.spongepowered.api.world.Location<World>> opt = block.getLocation();
		return opt.isPresent() ? new SpongeBlock(opt.get().getBlockRelative(getDirection(blockFace)).createSnapshot()) : this;
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
		return SpongeLocation.toCommon(block.getLocation().orElse(null));
	}

	@Override
	public boolean isLiquid() {
		String name = getType().getId().toLowerCase(Locale.ROOT);
		return name.contains("water") || name.contains("lava");
	}

	@Override
	public void setType(Material type) {
		org.spongepowered.api.world.Location<World> loc = block.getLocation().orElse(null);
		if(loc == null)
			return;
		ItemType item = (ItemType) type.getDefault();
		if(item.getBlock().isPresent())
			loc.setBlockType(item.getBlock().get());
	}

	@Override
	public boolean isWaterLogged() {
		return false;
	}
	
	@Override
	public Object getDefault() {
		return block;
	}

}
