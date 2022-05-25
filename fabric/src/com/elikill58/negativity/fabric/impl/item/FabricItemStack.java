package com.elikill58.negativity.fabric.impl.item;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.Material;

import net.minecraft.item.ItemStack;
import net.minecraft.util.registry.Registry;

public class FabricItemStack extends com.elikill58.negativity.api.item.ItemStack {
	
	private final ItemStack item;
	
	public FabricItemStack(ItemStack item) {
		this.item = item;
	}

	@Override
	public int getAmount() {
		return item.getCount();
	}

	@Override
	public Material getType() {
		return FabricItemRegistrar.getInstance().get(Registry.ITEM.getKey(item.getItem()).orElseThrow().getValue().getPath());
	}

	@Override
	public String getName() {
		return item.getName().asString();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean hasEnchant(Enchantment enchant) {
		// TODO fix contains & remove enchant
		return item.hasEnchantments() && item.getEnchantments().contains(getEnchantLevel(enchant));
	}

	@Override
	public int getEnchantLevel(Enchantment enchant) {
		// TODO fix enchant level
		return 0;
	}

	@Override
	public void addEnchant(Enchantment enchant, int level) {
		item.addEnchantment(FabricEnchants.getFabricEnchant(enchant), level);
	}

	@Override
	public void removeEnchant(Enchantment enchant) {
		item.getEnchantments().removeIf(nbt -> nbt.toString().contains(enchant.getId()));
	}
	
	@Override
	public com.elikill58.negativity.api.item.ItemStack clone() {
		return new FabricItemStack(item.copy());
	}

	@Override
	public Object getDefault() {
		return item;
	}
}
