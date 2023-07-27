package com.elikill58.negativity.spigot.impl.item;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.universal.Adapter;

public class SpigotItemStack extends ItemStack {

    private final org.bukkit.inventory.ItemStack item;

    public SpigotItemStack(org.bukkit.inventory.ItemStack item) {
        this.item = item;
    }

    @Override
    public int getAmount() {
        return item.getAmount();
    }

    @Override
    public Material getType() {
        return Adapter.getAdapter().getItemRegistrar().get(item.getType().name());
    }

    @Override
    public String getName() {
        return item.hasItemMeta() && item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
    }

    @Override
    public boolean hasEnchant(Enchantment enchant) {
        org.bukkit.enchantments.Enchantment en = SpigotEnchants.getEnchant(enchant);
        return en != null && item.containsEnchantment(en);
    }

    @Override
    public int getEnchantLevel(Enchantment enchant) {
        org.bukkit.enchantments.Enchantment en = SpigotEnchants.getEnchant(enchant);
        return en == null ? 0 : item.getEnchantments().getOrDefault(en, 0);
    }

    @Override
    public void addEnchant(Enchantment enchant, int level) {
        org.bukkit.enchantments.Enchantment en = SpigotEnchants.getEnchant(enchant);
        if (en != null)
            item.addUnsafeEnchantment(en, level);
    }

    @Override
    public void removeEnchant(Enchantment enchant) {
        org.bukkit.enchantments.Enchantment en = SpigotEnchants.getEnchant(enchant);
        if (en != null)
            item.removeEnchantment(en);
    }

    @Override
    public ItemStack clone() {
        return new SpigotItemStack(item.clone());
    }

    @Override
    public Object getDefault() {
        return item;
    }
}
