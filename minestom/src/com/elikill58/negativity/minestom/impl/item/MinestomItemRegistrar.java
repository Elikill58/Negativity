package com.elikill58.negativity.minestom.impl.item;

import java.util.HashMap;
import java.util.Locale;
import java.util.StringJoiner;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;

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
					return null; // ignore not found item
				sj.add(alias);
			}
			Adapter.getAdapter().getLogger().info("[MinestomItemRegistrar] Cannot find material " + id + sj);
			return null;
		});
	}
	
	private @Nullable Material findMaterial(String key) {
		net.minestom.server.item.Material m = net.minestom.server.item.Material.fromNamespaceId((key.contains(":") ? key : "minecraft:" + key).toLowerCase(Locale.ROOT));
		return m == null ? null : new MinestomMaterial(m);
	}
}
