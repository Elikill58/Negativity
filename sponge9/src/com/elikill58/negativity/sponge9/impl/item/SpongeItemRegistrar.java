package com.elikill58.negativity.sponge9.impl.item;

import java.util.HashMap;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryHolder;
import org.spongepowered.api.registry.RegistryTypes;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge9.impl.block.SpongeBlockMaterial;

public class SpongeItemRegistrar extends ItemRegistrar {
	
	private static final Logger LOGGER = LogManager.getLogger(SpongeItemRegistrar.class);
	
	private final HashMap<String, Material> cache = new HashMap<>();
	
	@Override
	public @Nullable Material get(String id, String... aliases) {
		return cache.computeIfAbsent(id, key -> {
			RegistryHolder registries = Sponge.game();
			ResourceKey resourceKey = parse(id);
			
			Registry<BlockType> blockTypeRegistry = registries.registry(RegistryTypes.BLOCK_TYPE);
			@Nullable BlockType blockItemType = blockTypeRegistry.findValue(resourceKey).orElse(null);
			if (blockItemType != null && !returnedDefaultValue(resourceKey, blockItemType)) {
				return new SpongeBlockMaterial(blockItemType);
			}
			
			Registry<ItemType> itemTypeRegistry = registries.registry(RegistryTypes.ITEM_TYPE);
			@Nullable ItemType itemType = itemTypeRegistry.findValue(resourceKey).orElse(null);
			if (itemType != null && !returnedDefaultValue(resourceKey, itemType)) {
				return new SpongeItemMaterial(itemType);
			}
			
			for (String alias : aliases) {
				ResourceKey aliasKey = parse(alias);
				@Nullable BlockType aliasedBlockType = blockTypeRegistry.findValue(aliasKey).orElse(null);
				if (aliasedBlockType != null && !returnedDefaultValue(aliasKey, aliasedBlockType)) {
					return new SpongeBlockMaterial(aliasedBlockType);
				}
				@Nullable ItemType aliasedItemType = itemTypeRegistry.findValue(aliasKey).orElse(null);
				if (aliasedItemType != null && !returnedDefaultValue(aliasKey, aliasedItemType)) {
					return new SpongeItemMaterial(aliasedItemType);
				}
			}
			
			LOGGER.warn("[SpongeItemRegistrar] Could not find material : " + id + ", " + String.join(", ", aliases));
			return null;
		});
	}
	
	@SuppressWarnings("unchecked")
	private static boolean returnedDefaultValue(ResourceKey key, ItemType value) {
		if (key.namespace().equals("minecraft") && key.value().equals("air")) {
			return false;
		}
		return value.isAnyOf(ItemTypes.AIR);
	}
	
	@SuppressWarnings("unchecked")
	private static boolean returnedDefaultValue(ResourceKey key, BlockType value) {
		if (key.namespace().equals("minecraft") && key.value().equals("air")) {
			return false;
		}
		return value.isAnyOf(BlockTypes.AIR);
	}
	
	private ResourceKey parse(String base) {
		return ResourceKey.resolve(base.toLowerCase(Locale.ROOT));
	}
}
