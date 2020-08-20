package com.elikill58.negativity.api.item;

import java.util.List;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class ItemBuilder {

    public abstract ItemBuilder displayName(@Nullable String displayName);

    public abstract ItemBuilder resetDisplayName();

	public abstract ItemBuilder enchant(Enchantment enchantment, int level);
	
    public abstract ItemBuilder unsafeEnchant(Enchantment enchantment, int level);

    public abstract ItemBuilder amount(int amount);
    
	public abstract ItemBuilder color(DyeColor color);

    public abstract ItemBuilder lore(List<String> lore);

    public abstract ItemBuilder lore(String... lore);

    public abstract ItemBuilder addToLore(String... loreToAdd);

    public abstract ItemStack build();
	
	public static ItemBuilder Builder(Material type) {
		return Adapter.getAdapter().createItemBuilder(type);
	}
	
	public static ItemBuilder Builder(Player owner) {
		return Adapter.getAdapter().createSkullItemBuilder(owner);
	}
}
