package com.elikill58.negativity.minestom.impl.item;

import java.util.HashMap;
import java.util.Locale;
import java.util.StringJoiner;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;

import net.minestom.server.instance.block.Block;

public class MinestomItemRegistrar extends ItemRegistrar {

	private final HashMap<String, Material> cache = new HashMap<>();

	@Override
	public @Nullable Material get(String id, String... aliases) {
		return cache.computeIfAbsent(id, key -> {
			Material optId = findMaterial(key);
			if (optId != null) {
				return optId;
			}
			
			for (String alias : aliases) {
				Material aliasedMaterial = findMaterial(alias);
				if (aliasedMaterial != null) {
					return aliasedMaterial;
				}
			}
			
			StringJoiner sj = new StringJoiner(", ", " : ", "");
			for(String alias : aliases) {
				if(alias.equals(Materials.IGNORE_KEY))
					return createDefault(id); // ignore not found item
				sj.add(alias);
			}
			Adapter.getAdapter().getLogger().info("[MinestomItemRegistrar] Cannot find material " + id + sj);
			return createDefault(id);
		});
	}
	
	private @Nullable Material findMaterial(String key) {
		String namespaced = (key.contains(":") ? key : "minecraft:" + key).toLowerCase(Locale.ROOT);
		net.minestom.server.item.Material m = net.minestom.server.item.Material.fromKey(namespaced);
		if (m != null)
			return new MinestomMaterial(m);
		// block-only materials (water, lava, fire, ...) are not in the item registry
		Block b = Block.fromKey(namespaced);
		return b == null ? null : new MinestomMaterial(b);
	}
}
