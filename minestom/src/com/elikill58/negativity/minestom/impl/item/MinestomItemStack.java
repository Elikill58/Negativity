package com.elikill58.negativity.minestom.impl.item;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.Material;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;

public class MinestomItemStack extends com.elikill58.negativity.api.item.ItemStack {
	
	private ItemStack item;
	
	public MinestomItemStack(ItemStack item) {
		this.item = item;
	}

	@Override
	public int getAmount() {
		return item.amount();
	}

	@Override
	public Material getType() {
		return new MinestomMaterial(item.material());
	}

	@Override
	public String getName() {
		return LegacyComponentSerializer.legacySection().serialize(item.get(ItemComponent.ITEM_NAME));
	}

	@Override
	public boolean hasEnchant(Enchantment enchant) {
		return item.get(ItemComponent.ENCHANTMENTS).enchantments().containsKey(MinestomEnchants.getEnchant(enchant));
	}

	@Override
	public int getEnchantLevel(Enchantment enchant) {
		Integer lvl = item.get(ItemComponent.ENCHANTMENTS).enchantments().get(MinestomEnchants.getEnchant(enchant));
		return lvl == null ? 0 : lvl;
	}

	@Override
	public void addEnchant(Enchantment enchant, int level) {
		this.item = (ItemStack) new MinestomItemBuilder(this).enchant(enchant, level).build().getDefault();
	}

	@Override
	public void removeEnchant(Enchantment enchant) {
		this.item = (ItemStack) new MinestomItemBuilder(this).unenchant(enchant).build().getDefault();
	}
	
	@Override
	public com.elikill58.negativity.api.item.ItemStack clone() {
		return this; // this item is immutable
	}

	@Override
	public Object getDefault() {
		return item;
	}
}
