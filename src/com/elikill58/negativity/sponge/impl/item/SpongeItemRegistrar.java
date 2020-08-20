package com.elikill58.negativity.sponge.impl.item;

import java.util.HashMap;
import java.util.Optional;
import java.util.StringJoiner;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.item.ItemType;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge.SpongeNegativity;

public class SpongeItemRegistrar extends ItemRegistrar {

	private final HashMap<String, Material> cache = new HashMap<>();

	@Override
	public Material get(String id, String... alias) {
		return cache.computeIfAbsent(id, key -> {
			Optional<ItemType> optId = Sponge.getRegistry().getType(ItemType.class, id);
			if(optId.isPresent())
				return new SpongeMaterial(optId.get());
			Optional<BlockType> optBlock = Sponge.getRegistry().getType(BlockType.class, id);
			if(optBlock.isPresent() && optBlock.get().getItem().isPresent())
				return new SpongeMaterial(optBlock.get().getItem().get());
			for(String tempID : alias) {
				Optional<ItemType> optAlias = Sponge.getRegistry().getType(ItemType.class, parse(tempID));
				if(optAlias.isPresent())
					return new SpongeMaterial(optAlias.get());
			}
			StringJoiner sj = new StringJoiner(", ", " : ", "");
			for(String tempAlias : alias) sj.add(tempAlias + " (" + parse(tempAlias) + ")");
			SpongeNegativity.getInstance().getLogger().info("[SpongeItemRegistrar] Cannot find material " + id + sj.toString());
			return null;
		});
	}
	
	private String parse(String base) {
		StringJoiner sj = new StringJoiner("");
		for(String ch : base.split("")) {
			if(ch.equals("["))
				return sj.toString().replaceAll("minecraft:", "");
			else
				sj.add(ch);
		}
		return sj.toString().replaceAll("minecraft:", "");
	}
}
