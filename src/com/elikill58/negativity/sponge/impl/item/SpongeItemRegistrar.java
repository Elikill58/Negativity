package com.elikill58.negativity.sponge.impl.item;

import java.util.HashMap;
import java.util.Optional;

import org.spongepowered.api.Sponge;
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
			for(String tempID : alias) {
				Optional<ItemType> optAlias = Sponge.getRegistry().getType(ItemType.class, tempID);
				if(optAlias.isPresent())
					return new SpongeMaterial(optAlias.get());
			}
			SpongeNegativity.getInstance().getLogger().info("[SpongeItemRegistrar] Cannot find material " + id);
			return null;
		});
	}
}
