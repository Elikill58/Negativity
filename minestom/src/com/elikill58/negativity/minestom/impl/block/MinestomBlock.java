package com.elikill58.negativity.minestom.impl.block;

import java.util.Locale;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.minestom.impl.location.MinestomLocation;

import net.minestom.server.coordinate.Point;
import net.minestom.server.instance.Instance;

public class MinestomBlock extends Block {

	private final net.minestom.server.instance.block.Block block;
	private final Instance w;
	private final Point position;
	
	public MinestomBlock(net.minestom.server.instance.block.Block block, Instance w, Point position) {
		this.block = block;
		this.w = w;
		this.position = position;
	}

	@Override
	public Material getType() {
		return ItemRegistrar.getInstance().get(block.key().asString());
	}

	@Override
	public int getX() {
		return position.blockX();
	}

	@Override
	public int getY() {
		return position.blockY();
	}

	@Override
	public int getZ() {
		return position.blockZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		Point pos = position.add(blockFace.getModX(), blockFace.getModY(), blockFace.getModZ());
		return new MinestomBlock(w.getBlock(pos), w, pos);
	}

	@Override
	public Location getLocation() {
		return MinestomLocation.toCommon(w, position);
	}

	@Override
	public boolean isLiquid() {
		String name = getType().getId().toLowerCase(Locale.ROOT);
		return name.contains("water") || name.contains("lava");
	}

	@Override
	public void setType(Material type) {
		w.setBlock(position, ((net.minestom.server.item.Material) type.getDefault()).block());
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
