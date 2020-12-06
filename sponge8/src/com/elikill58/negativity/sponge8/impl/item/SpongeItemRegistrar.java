package com.elikill58.negativity.sponge8.impl.item;

import java.util.HashMap;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.registry.CatalogRegistry;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;

import net.kyori.adventure.key.Key;

public class SpongeItemRegistrar extends ItemRegistrar {
	
	private static final Logger LOGGER = LogManager.getLogger(SpongeItemRegistrar.class);
	
	private final HashMap<String, Material> cache = new HashMap<>();
	
	@Override
	public Material get(String id, String... alias) {
		return cache.computeIfAbsent(id, key -> {
			CatalogRegistry registry = Sponge.getRegistry().getCatalogRegistry();
			Optional<ItemType> optId = registry.get(ItemType.class, parse(id));
			if (optId.isPresent()) {
				return new SpongeMaterial(optId.get());
			}
			
			Optional<BlockType> optBlock = registry.get(BlockType.class, parse(id));
			if (optBlock.isPresent() && optBlock.get().getItem().isPresent()) {
				return new SpongeMaterial(optBlock.get().getItem().get());
			}
			
			for (String tempID : alias) {
				Optional<ItemType> optAlias = registry.get(ItemType.class, parse(tempID));
				if (optAlias.isPresent()) {
					return new SpongeMaterial(optAlias.get());
				}
			}
			
			StringJoiner sj = new StringJoiner(", ");
			for (String tempAlias : alias) {
				//sj.add(tempAlias + " (" + parse(tempAlias) + ")");
				sj.add(tempAlias);
			}
			
			LOGGER.warn("[SpongeItemRegistrar] Could not find material : " + id + sj);
			return null;
		});
	}
	
	private Key parse(String base) {
		return Key.key(base);
		//StringJoiner sj = new StringJoiner("");
		//for(String ch : base.split("")) {
		//	if(ch.equals("["))
		//		return sj.toString();
		//	else
		//		sj.add(ch);
		//}
		//return sj.toString();
	}
}
