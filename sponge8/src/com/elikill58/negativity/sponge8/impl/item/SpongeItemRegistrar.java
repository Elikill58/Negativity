package com.elikill58.negativity.sponge8.impl.item;

import java.util.HashMap;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryHolder;
import org.spongepowered.api.registry.RegistryTypes;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;

public class SpongeItemRegistrar extends ItemRegistrar {
	
	private static final Logger LOGGER = LogManager.getLogger(SpongeItemRegistrar.class);
	
	private final HashMap<String, Material> cache = new HashMap<>();
	
	@Override
	public @Nullable Material get(String id, String... aliases) {
		return cache.computeIfAbsent(id, key -> {
			RegistryHolder registries = Sponge.getGame().registries();
			ResourceKey resourceKey = parse(id);
			
			Registry<ItemType> itemTypeRegistry = registries.registry(RegistryTypes.ITEM_TYPE);
			@Nullable ItemType itemType = itemTypeRegistry.findValue(resourceKey).orElse(null);
			if (itemType != null && !returnedDefaultValue(resourceKey, itemType)) {
				return new SpongeMaterial(itemType);
			}
			
			@Nullable ItemType blockItemType = registries.registry(RegistryTypes.BLOCK_TYPE).findValue(resourceKey)
				.flatMap(BlockType::getItem).orElse(null);
			if (blockItemType != null && !returnedDefaultValue(resourceKey, blockItemType)) {
				return new SpongeMaterial(blockItemType);
			}
			
			for (String alias : aliases) {
				ResourceKey aliasKey = parse(alias);
				@Nullable ItemType aliasedItemType = itemTypeRegistry.findValue(aliasKey).orElse(null);
				if (aliasedItemType != null && !returnedDefaultValue(aliasKey, aliasedItemType)) {
					return new SpongeMaterial(aliasedItemType);
				}
			}
			
			LOGGER.warn("[SpongeItemRegistrar] Could not find material : " + id + ", " + String.join(", ", aliases));
			return null;
		});
	}
	
	private static boolean returnedDefaultValue(ResourceKey key, ItemType value) {
		if (key.namespace().equals("minecraft") && key.value().equals("air")) {
			return false;
		}
		return value.isAnyOf(ItemTypes.AIR);
	}
	
	private ResourceKey parse(String base) {
		return ResourceKey.resolve(base.toLowerCase(Locale.ROOT));
	}
}
