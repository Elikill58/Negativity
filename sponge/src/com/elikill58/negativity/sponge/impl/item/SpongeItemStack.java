package com.elikill58.negativity.sponge.impl.item;

import java.util.List;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.item.EnchantmentData;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
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
		return SpongeItemRegistrar.getInstance().get(item.getType().getId(), item.getType().getName());
	}

	@Override
	public String getName() {
		return item.get(Keys.DISPLAY_NAME).orElse(Text.EMPTY).toPlain();
	}

	@Override
	public boolean hasEnchant(Enchantment enchant) {
		List<org.spongepowered.api.item.enchantment.Enchantment> enchantments = item.getOrNull(Keys.ITEM_ENCHANTMENTS);
		if (enchantments == null) {
			return false;
		}
		for (org.spongepowered.api.item.enchantment.Enchantment enchantment : enchantments) {
			if (enchantment.getType().getId().equalsIgnoreCase(enchant.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getEnchantLevel(Enchantment enchant) {
		EnchantmentData data = item.getOrCreate(EnchantmentData.class).get();
		List<org.spongepowered.api.item.enchantment.Enchantment> list = data.getListValue().filter((en) -> en.getType().equals(getEnchantType(enchant))).get();
		return list.isEmpty() ? 0 : list.get(0).getLevel();
	}

	@Override
	public void addEnchant(Enchantment enchant, int level) {
		item.getOrCreate(EnchantmentData.class).get().addElement(org.spongepowered.api.item.enchantment.Enchantment.builder().type(getEnchantType(enchant)).level(level).build());
	}
	
	private EnchantmentType getEnchantType(Enchantment enchant) {
		switch (enchant) {
		case DIG_SPEED:
			return EnchantmentTypes.EFFICIENCY;
		case THORNS:
			return EnchantmentTypes.THORNS;
		default:
			throw new RuntimeException("Unhandled enchantment " + enchant);
		}
	}

	@Override
	public void removeEnchant(Enchantment enchant) {
		EnchantmentData data = item.getOrCreate(EnchantmentData.class).get();
		data.getListValue().filter((en) -> en.getType().equals(getEnchantType(enchant))).get().forEach((e) -> data.remove(e));
	}

	@Override
	public Object getDefault() {
		return item;
	}
}
