package com.elikill58.negativity.sponge.impl.item;

import java.util.stream.Collectors;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.Material;

public class SpongeItemStack extends com.elikill58.negativity.api.item.ItemStack {
	
	private final ItemStack item;
	
	public SpongeItemStack(ItemStack item) {
		this.item = item;
	}

	@Override
	public int getAmount() {
		return item.getQuantity();
	}

	@Override
	public Material getType() {
		return null;
	}

	@Override
	public String getName() {
		return item.get(Keys.DISPLAY_NAME).orElse(Text.EMPTY).toPlain();
	}

	@Override
	public boolean hasEnchant(Enchantment enchant) {
		return !item.get(Keys.ITEM_ENCHANTMENTS).get().stream().filter((en) -> en.getType().getId().equalsIgnoreCase(enchant.name())).collect(Collectors.toList()).isEmpty();
	}

	@Override
	public int getEnchantLevel(Enchantment enchant) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addEnchant(Enchantment enchant, int level) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEnchant(Enchantment enchant) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getDefault() {
		return item;
	}
}
