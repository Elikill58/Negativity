package com.elikill58.negativity.spigot.impl.block;

import org.bukkit.block.data.Waterlogged;

import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.block.BlockFace;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.universal.Version;

public class SpigotBlock extends Block {

	private final org.bukkit.block.Block block;

	public SpigotBlock(org.bukkit.block.Block block) {
		this.block = block;
	}

	@Override
	public Material getType() {
		try {
			return ItemRegistrar.getInstance().get(block.getType().name());
		} catch (NullPointerException e) { // shitty folia
			return Materials.AIR;
		}
	}

	@Override
	public int getX() {
		return block.getX();
	}

	@Override
	public int getY() {
		return block.getY();
	}

	@Override
	public int getZ() {
		return block.getZ();
	}

	@Override
	public Block getRelative(BlockFace blockFace) {
		return new SpigotBlock(block.getRelative(org.bukkit.block.BlockFace.valueOf(blockFace.name())));
	}

	@Override
	public Location getLocation() {
		return SpigotLocation.toCommon(block.getLocation());
	}

	@Override
	public boolean isLiquid() {
		return block.isLiquid();
	}

	@Override
	public void setType(Material type) {
		block.setType((org.bukkit.Material) type.getDefault());
	}
	
	@Override
	public boolean isWaterLogged() {
		return Version.getVersion().isNewerOrEquals(Version.V1_13) && (block instanceof Waterlogged) && ((Waterlogged) block).isWaterlogged();
	}

	@Override
	public Object getDefault() {
		return block;
	}
}
