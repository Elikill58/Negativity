package com.elikill58.negativity.api.item;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.universal.Adapter;

public abstract class ItemBuilder {

	/**
	 * Set the display name of the item
	 * If the display name is null, the item will take the basic minecraft name
	 * 
	 * @param displayName the new name
	 * @return this builder
	 */
    public abstract ItemBuilder displayName(@Nullable String displayName);

    /**
     * Reset display name.
     * Equals as {@link ItemBuilder#displayName} if you set a null name
     * 
     * @return this builder
     */
    public abstract ItemBuilder resetDisplayName();

    /**
     * Add enchant to item
     * 
     * @param enchantment the enchant name
     * @param level the level of the enchant
     * @return this builder
     */
	public abstract ItemBuilder enchant(Enchantment enchantment, int level);

    /**
     * Add flag to this item
     * 
     * @param itemFlag all flag to add
     * @return this builder
     */
	public abstract ItemBuilder itemFlag(ItemFlag... itemFlag);
	
	/**
	 * Add enchant to item without checking if it exist
	 * As essentials, this method allow you to add enchant with level at more than max allowed by default
	 * 
	 * @param enchantment the enchant name
	 * @param level the level of the enchant
	 * @return this builder
	 */
    public abstract ItemBuilder unsafeEnchant(Enchantment enchantment, int level);
    
    /**
     * (unsafe) Enchant the item if the condition success and hide it
     * 
     * @param b if the enchant should be applied
     * @return this builder
     */
    public ItemBuilder enchantIf(boolean b) {
    	if(b) {
    		unsafeEnchant(Enchantment.UNBREAKING, 1);
    		itemFlag(ItemFlag.HIDE_ENCHANTS);
    	}
    	return this;
    }
    
    /**
     * (unsafe) Enchant the item if the condition success and hide it
     * 
     * @param enchant the enchant to apply
     * @param level the level of the enchant
     * @param b if the enchant should be applied
     * @return this builder
     */
    public ItemBuilder enchantIf(Enchantment enchant, int level, boolean b) {
    	if(b) {
    		unsafeEnchant(enchant, level);
    		itemFlag(ItemFlag.HIDE_ENCHANTS);
    	}
    	return this;
    }

    /**
     * Set the amount of the item.
     * Default: 1
     * 
     * @param amount the new amount of item
     * @return this builder
     */
    public abstract ItemBuilder amount(int amount);
    
    /**
     * Edit the color
     * Work only with colorable item like leather
     * 
     * @param color the new item color
     * @return this builder
     */
	public abstract ItemBuilder color(DyeColor color);

	/**
	 * Set lore to current item
	 * 
	 * @param lore the new lore list
	 * @return this builder
	 */
    public abstract ItemBuilder lore(List<String> lore);

	/**
	 * Set lore to current item
	 * 
	 * @param lore the new lore list
	 * @return this builder
	 */
    public abstract ItemBuilder lore(String... lore);

	/**
	 * Add lore to current item
	 * 
	 * @param loreToAdd Lore lines which must to be added
	 * @return this builder
	 */
    public abstract ItemBuilder addToLore(String... loreToAdd);

    /**
     * Build the item.
     * 
     * @return the usable item
     */
    public abstract ItemStack build();
	
    /**
     * Create an ItemBuilder with a material.
     * 
     * @param type the material of the item
     * @return the item builder
     */
	public static ItemBuilder Builder(Material type) {
		return Adapter.getAdapter().createItemBuilder(type);
	}
	
    /**
     * Create an ItemBuilder with a default item.
     * 
     * @param item the beginning item
     * @return the item builder
     */
	public static ItemBuilder Builder(ItemStack item) {
		return Adapter.getAdapter().createItemBuilder(item);
	}
	
	/**
	 * Create an ItemBuilder with a material's name.
	 * Compatible with "type:byte" for 1.12 and less items
	 * 
	 * @param type the type descriptor for the desired material
	 * @return the item builder
	 */
	public static ItemBuilder Builder(String type) {
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
	
	/**
	 * Create an ItemBuilder for SkullItem
	 * 
	 * @param owner the owner of the skull
	 * @return the builder for skull item
	 */
	public static ItemBuilder Builder(OfflinePlayer owner) {
		return Adapter.getAdapter().createSkullItemBuilder(owner);
	}
	
	/**
	 * Get the skull item showed in negativity's inventory
	 * Display name and lore are already set, and are according to the given player cible
	 * 
	 * @param cible the owner of the skull item
	 * @return the skull item which will be set in inventory
	 */
	public static ItemStack getSkullItem(Player cible) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		return ItemBuilder.Builder(cible).displayName(cible.getName()).lore(ChatColor.GOLD + "UUID: " + cible.getUniqueId(), ChatColor.GREEN + "Version: " + cible.getPlayerVersion().getName(), ChatColor.GREEN + "Platform: " + (np.isBedrockPlayer() ? "Bedrock" : "Java")).build();
	}
	
	public static ItemStack getSkullItem(OfflinePlayer cible) {
		if(cible instanceof Player)
			return getSkullItem((Player) cible);
		return ItemBuilder.Builder(cible).displayName(cible.getName()).lore(ChatColor.RED + "Offline", ChatColor.GOLD + "UUID: " + cible.getUniqueId()).build();
	}
}
