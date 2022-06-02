package com.elikill58.negativity.spigot.impl.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.colors.DyeColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.universal.Version;

public class SpigotItemBuilder extends ItemBuilder {

    private ItemStack itemStack;
    private ItemMeta itemMeta;

    @SuppressWarnings("deprecation")
	public SpigotItemBuilder(Material type) {
    	this.itemStack = new ItemStack((org.bukkit.Material) type.getDefault());
    	byte damage = ((SpigotMaterial) type).getDamage();
    	this.itemMeta = (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
    	if(!Version.getVersion().isNewerOrEquals(Version.V1_13) && damage > 0) {
    		if(!Version.getVersion().equals(Version.V1_7) && itemMeta instanceof BannerMeta)
    			((BannerMeta) this.itemMeta).setBaseColor(org.bukkit.DyeColor.getByDyeData(damage));
    		else
    			this.itemStack.setDurability(damage);
    	}
    }

    @SuppressWarnings("deprecation")
	public SpigotItemBuilder(com.elikill58.negativity.api.item.ItemStack item) {
    	this.itemStack = (ItemStack) item.getDefault();
    	byte damage = ((SpigotMaterial) item.getType()).getDamage();
    	this.itemMeta = (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
    	if(!Version.getVersion().isNewerOrEquals(Version.V1_13) && damage > 0) {
    		if(!Version.getVersion().equals(Version.V1_7) && itemMeta instanceof BannerMeta)
    			((BannerMeta) this.itemMeta).setBaseColor(org.bukkit.DyeColor.getByDyeData(damage));
    		else
    			this.itemStack.setDurability(damage);
    	}
    }

    public SpigotItemBuilder(String material) {
    	this.itemStack = com.elikill58.negativity.spigot.utils.Utils.getItemFromString(material);
    	this.itemMeta = (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
    }

	public SpigotItemBuilder(OfflinePlayer owner) {
    	this.itemStack = SpigotVersionAdapter.getVersionAdapter().createSkull((org.bukkit.OfflinePlayer) owner.getDefault());
    	this.itemMeta = (itemStack.hasItemMeta() ? itemStack.getItemMeta() : Bukkit.getItemFactory().getItemMeta(itemStack.getType()));
	}

	@Override
    public ItemBuilder displayName(@Nullable String displayName) {
        this.itemMeta.setDisplayName(ChatColor.RESET + Utils.coloredMessage(displayName));
        return this;
    }

    @Override
    public ItemBuilder resetDisplayName() {
        return this.displayName(null);
    }
    
    @Override
    public ItemBuilder itemFlag(ItemFlag... itemFlag) {
    	for(ItemFlag flag : itemFlag)
    		itemMeta.addItemFlags(org.bukkit.inventory.ItemFlag.valueOf(flag.name()));
    	return this;
    }

    @Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		org.bukkit.enchantments.Enchantment en = SpigotEnchants.getEnchant(enchantment);
		if(en != null)
			this.itemMeta.addEnchant(en, level, true);
        return this;
    }

    @Override
    public ItemBuilder unsafeEnchant(Enchantment enchantment, int level) {
		org.bukkit.enchantments.Enchantment en = SpigotEnchants.getEnchant(enchantment);
		if(en != null)
			this.itemMeta.addEnchant(en, level, true);
        return this;
    }

    @Override
    public ItemBuilder amount(int amount) {
        this.itemStack.setAmount(amount);
        return this;
    }
    
	@SuppressWarnings("deprecation")
    @Override
	public ItemBuilder color(DyeColor color) {
		itemStack.setDurability(color.getColorFor(new SpigotMaterial(itemStack.getType())));
		if(Version.getVersion().isNewerOrEquals(Version.V1_13)) {
			ItemMeta meta = itemStack.getItemMeta();
			((Damageable) meta).setDamage(color.getWool());
			itemStack.setItemMeta(meta);
		}
        return this;
    }

    @Override
    public ItemBuilder lore(List<String> lore) {
    	return lore(lore.toArray(new String[] {}));
    }

    @Override
    public ItemBuilder lore(String... lore) {
        List<String> list = this.itemMeta.hasLore() ? this.itemMeta.getLore() : new ArrayList<>();
    	for(String s : lore)
    		for(String temp : s.split("\\n"))
        		for(String tt : temp.split("/n"))
        			list.add(Utils.coloredMessage(tt));
        this.itemMeta.setLore(list);
        return this;
    }

    @Override
    public ItemBuilder addToLore(String... loreToAdd) {
        return lore(loreToAdd);
    }

    @Override
    public com.elikill58.negativity.api.item.ItemStack build() {
        this.itemStack.setItemMeta(this.itemMeta);
        return new SpigotItemStack(itemStack);
    }
}
