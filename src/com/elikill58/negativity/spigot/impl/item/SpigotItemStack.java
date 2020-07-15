package com.elikill58.negativity.spigot.impl.item;

import com.elikill58.negativity.common.item.Enchantment;
import com.elikill58.negativity.common.item.ItemStack;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.universal.adapter.Adapter;

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

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasEnchant(Enchantment enchant) {
		return item.containsEnchantment(org.bukkit.enchantments.Enchantment.getByName(enchant.name()));
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getEnchantLevel(Enchantment enchant) {
		return item.getEnchantments().get(org.bukkit.enchantments.Enchantment.getByName(enchant.name()));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void addEnchant(Enchantment enchant, int level) {
		item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.getByName(enchant.name()), level);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeEnchant(Enchantment enchant) {
		item.removeEnchantment(org.bukkit.enchantments.Enchantment.getByName(enchant.name()));
	}

	@Override
	public Object getDefaultItem() {
		return item;
	}
}
