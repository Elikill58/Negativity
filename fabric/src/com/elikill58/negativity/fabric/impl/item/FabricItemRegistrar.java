package com.elikill58.negativity.fabric.impl.item;

import java.util.HashMap;
import java.util.Optional;
import java.util.StringJoiner;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.fabric.FabricNegativity;
import com.elikill58.negativity.universal.utils.UniversalUtils;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;

public class FabricItemRegistrar extends ItemRegistrar {

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
			FabricNegativity.getInstance().getLogger().info("[FabricItemRegistrar] Cannot find material " + id + sj);
			return null;
		});
	}
	
	private @Nullable Material findMaterial(String key) {
		if(UniversalUtils.isInteger(key))
			return new FabricMaterial(Item.byRawId(Integer.parseInt(key)));
		Optional<Item> opt = Registry.ITEM.stream().filter(i -> i.getTranslationKey().contains(key)).findFirst();
		return opt.isPresent() ? new FabricMaterial(opt.get()) : null;
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
