package com.elikill58.negativity.sponge7.impl.item;

import java.util.HashMap;
import java.util.Optional;
import java.util.StringJoiner;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge7.SpongeNegativity;

public class SpongeItemRegistrar extends ItemRegistrar {

	private final HashMap<String, Material> cache = new HashMap<>();

	@Override
	public @Nullable Material get(String id, String... aliases) {
		return cache.computeIfAbsent(id, key -> {
			Material optId = findMaterial(parse(key));
			if (optId != null) {
				return optId;
			}
			
			String[] parsedAliases = new String[aliases.length];
			for (int i = 0, aliasesLength = aliases.length; i < aliasesLength; i++) {
				parsedAliases[i] = parse(aliases[i]);
			}
			
			for (String alias : parsedAliases) {
				Material aliasedMaterial = findMaterial(alias);
				if (aliasedMaterial != null) {
					return aliasedMaterial;
				}
			}
			
			StringJoiner sj = new StringJoiner(", ", " : ", "");
			for(String alias : parsedAliases) sj.add(alias + " (" + parse(alias) + ")");
			SpongeNegativity.getInstance().getLogger().info("[SpongeItemRegistrar] Cannot find material " + id + sj);
			return null;
		});
	}
	
	private @Nullable Material findMaterial(String key) {
		Optional<ItemType> optId = Sponge.getRegistry().getType(ItemType.class, key);
		if (optId.isPresent()) {
			return new SpongeMaterial(optId.get());
		}
		Optional<BlockType> optBlock = Sponge.getRegistry().getType(BlockType.class, key);
		if (optBlock.isPresent()) {
			Optional<ItemType> item = optBlock.get().getItem();
			if (item.isPresent()) {
				return new SpongeMaterial(item.get());
			} else {
				return new SpongeBlockMaterial(optBlock.get());
			}
		}
		return null;
	}
	
	private String parse(String base) {
		StringJoiner sj = new StringJoiner("");
		for(String ch : base.split("")) {
			if(ch.equals("["))
				return sj.toString();
			else
				sj.add(ch);
		}
		return sj.toString();
	}
}
