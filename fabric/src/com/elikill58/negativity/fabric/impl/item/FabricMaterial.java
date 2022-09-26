package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Material;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class FabricMaterial extends Material {

	private final String id;
	private final Item itemType;
	private final Block blockType;
	
	public FabricMaterial(Item itemType) {
		this.id = Registry.ITEM.getId(itemType).toString();
		this.itemType = itemType;
		this.blockType = net.minecraft.block.Block.getBlockFromItem(itemType);
	}
	
	public FabricMaterial(Block blockType) {
		this.id = Registry.BLOCK.getId(blockType).toString();
		this.itemType = blockType.asItem();
		this.blockType = blockType;
	}
	
	@Override
	public boolean isSolid() {
		return blockType.getDefaultState().getMaterial().isSolid();
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public boolean isTransparent() {
		return !blockType.getDefaultState().getMaterial().blocksLight();
	}

	@Override
	public Object getDefault() {
		return itemType;
	}
}
