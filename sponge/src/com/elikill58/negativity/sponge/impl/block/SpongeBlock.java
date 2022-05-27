package com.elikill58.negativity.sponge.impl.block;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.server.ServerLocation;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.sponge.impl.location.SpongeWorld;
import com.elikill58.negativity.sponge.utils.Utils;

public class SpongeBlock extends Block {
	
	private @Nullable Material cachedMaterial;
	private final BlockSnapshot block;
	
	public SpongeBlock(BlockSnapshot block) {
		this.block = block;
	}
	
	@Override
	public Material getType() {
		if (this.cachedMaterial == null) {
			ResourceKey key = Utils.getKey(block.state().type());
			this.cachedMaterial = ItemRegistrar.getInstance().get(key.asString());
		}
		return this.cachedMaterial;
	}
	
	@Override
	public int getX() {
		return block.position().x();
	}
	
	@Override
	public int getY() {
		return block.position().y();
	}
	
	@Override
	public int getZ() {
		return block.position().z();
	}
	
	@Override
	public Block getRelative(BlockFace blockFace) {
		return block.location()
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
		World world = Sponge.server().worldManager().world(block.world())
			.map(SpongeWorld::new)
			.orElseThrow(() -> new IllegalStateException("Failed to get world " + block.world() + " from Block"));
		return new Location(world, getX(), getY(), getZ());
	}
	
	@Override
	public boolean isLiquid() {
		String name = getType().getId().toLowerCase(Locale.ROOT);
		return name.contains("water") || name.contains("lava");
	}
	
	@Override
	public void setType(Material type) {
		ServerLocation loc = block.location().orElse(null);
		if (loc != null) {
			BlockType blockType = Utils.getBlockType(type);
			if (blockType != null) {
				loc.setBlockType(blockType);
			}
		}
	}
	
	@Override
	public boolean isWaterLogged() {
		return block.getOrElse(Keys.IS_WATERLOGGED, false);
	}
	
	@Override
	public Object getDefault() {
		return block;
	}
	
}
