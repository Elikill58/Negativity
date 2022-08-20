package com.elikill58.negativity.minestom.impl.block;

import java.util.Locale;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.minestom.impl.location.FabricLocation;

import net.minestom.server.registry.Registry;

public class MinestomBlock extends Block {

	private final net.minestom.server.instance.block.Block block;
	private final BlockPos position;
	private final World world;
	
	public MinestomBlock(net.minestom.server.instance.block.Block block, World w, BlockPos position) {
		this.block = block;
		this.position = position;
		this.world = w;
	}

	@Override
	public Material getType() {
		String id = Registry.BLOCK.getKey(block).orElseThrow().getValue().getPath();
		return ItemRegistrar.getInstance().get(id);
	}

	@Override
	public int getX() {
		return position.getX();
	}

	@Override
	public int getY() {
		return position.getY();
	}

	@Override
	public int getZ() {
		return position.getZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		BlockPos pos = getDirection(position, blockFace);
		return new MinestomBlock(world.getBlockState(pos).getBlock(), world, pos);
	}
	
	public BlockPos getDirection(BlockPos pos, BlockFace bf) {
		return new BlockPos(pos.getX() + bf.getModX(), pos.getY() + bf.getModY(), pos.getZ() + bf.getModZ());
	}

	@Override
	public Location getLocation() {
		return FabricLocation.toCommon(world, position);
	}

	@Override
	public boolean isLiquid() {
		String name = getType().getId().toLowerCase(Locale.ROOT);
		return name.contains("water") || name.contains("lava");
	}

	@Override
	public void setType(Material type) {
		// TODO change material type
		//world.setBlockState(position, ((Item) type.getDefault()));
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
