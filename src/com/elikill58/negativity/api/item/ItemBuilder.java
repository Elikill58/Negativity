package com.elikill58.negativity.api.item;

import java.util.List;

import javax.annotation.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
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
	
	/**
	 * Create an ItemBuilder for SkullItem
	 * 
	 * @param owner the owner of the skull
	 * @return the builder for skull item
	 */
	public static ItemBuilder Builder(Player owner) {
		return Adapter.getAdapter().createSkullItemBuilder(owner);
	}
	
	public static ItemStack getSkullItem(Player cible) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		return ItemBuilder.Builder(cible).displayName(cible.getName()).lore(ChatColor.GOLD + "UUID: " + cible.getUniqueId(), ChatColor.GREEN + "Version: " + cible.getPlayerVersion().getName(), ChatColor.GREEN + "Platform: " + (np.isBedrockPlayer() ? "Bedrock" : "Java")).build();
	}
}
