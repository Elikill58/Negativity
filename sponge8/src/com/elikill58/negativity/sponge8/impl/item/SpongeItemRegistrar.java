package com.elikill58.negativity.sponge8.impl.item;

import java.util.HashMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryHolder;
import org.spongepowered.api.registry.RegistryTypes;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;

public class SpongeItemRegistrar extends ItemRegistrar {
	
	private static final Logger LOGGER = LogManager.getLogger(SpongeItemRegistrar.class);
	
	private final HashMap<String, Material> cache = new HashMap<>();
	
	@Override
	public @Nullable Material get(String id, String... alias) {
		return cache.computeIfAbsent(id, key -> {
			RegistryHolder registries = Sponge.getGame().registries();
			ResourceKey resourceKey = parse(id);
			
			Registry<ItemType> itemTypeRegistry = registries.registry(RegistryTypes.ITEM_TYPE);
			Optional<ItemType> maybeItemType = itemTypeRegistry.findValue(resourceKey);
			if (maybeItemType.isPresent()) {
				return new SpongeMaterial(maybeItemType.get());
			}
			
			Optional<BlockType> maybeBlockType = registries.registry(RegistryTypes.BLOCK_TYPE).findValue(resourceKey);
			if (maybeBlockType.isPresent() && maybeBlockType.get().getItem().isPresent()) {
				return new SpongeMaterial(maybeBlockType.get().getItem().get());
			}
			
			for (String tempID : alias) {
				Optional<ItemType> maybeAliasedItemType = itemTypeRegistry.findValue(parse(tempID));
				if (maybeAliasedItemType.isPresent()) {
					return new SpongeMaterial(maybeAliasedItemType.get());
				}
			}
			
			LOGGER.warn("[SpongeItemRegistrar] Could not find material : " + id + String.join(", ", alias));
			return null;
		});
	}
	
	private ResourceKey parse(String base) {
		return ResourceKey.resolve(base);
	}
}
