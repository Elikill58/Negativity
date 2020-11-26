package com.elikill58.negativity.sponge.impl.item;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.Material;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer;

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
		return SpongeItemRegistrar.getInstance().get(item.getType().key().asString(), item.getType().key().value());
	}

	@Override
	public String getName() {
		return item.get(Keys.DISPLAY_NAME)
			.map(component -> PlainComponentSerializer.plain().serialize(component))
			.orElse(null);
	}

	@Override
	public boolean hasEnchant(Enchantment enchant) {
		List<org.spongepowered.api.item.enchantment.Enchantment> enchantments = item.getOrNull(Keys.APPLIED_ENCHANTMENTS);
		if (enchantments == null) {
			return false;
		}
		for (org.spongepowered.api.item.enchantment.Enchantment enchantment : enchantments) {
			if (enchantment.getType().key().asString().equalsIgnoreCase(enchant.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int getEnchantLevel(Enchantment enchant) {
		List<org.spongepowered.api.item.enchantment.Enchantment> enchantments = item.getOrNull(Keys.APPLIED_ENCHANTMENTS);
		if (enchantments == null) {
			return 0;
		}
		for (org.spongepowered.api.item.enchantment.Enchantment enchantment : enchantments) {
			if (enchantment.getType().key().asString().equalsIgnoreCase(enchant.getId())) {
				return enchantment.getLevel();
			}
		}
		return 0;
	}

	@Override
	public void addEnchant(Enchantment enchant, int level) {
		item.offerSingle(Keys.APPLIED_ENCHANTMENTS, org.spongepowered.api.item.enchantment.Enchantment.of(getEnchantType(enchant), level));
	}
	
	private EnchantmentType getEnchantType(Enchantment enchant) {
		return Sponge.getRegistry().getCatalogRegistry()
			.get(EnchantmentType.class, Key.key(enchant.getId()))
			.orElseThrow(() -> new RuntimeException("Unknown enchantment " + enchant.getId()));
	}

	@Override
	public void removeEnchant(Enchantment enchant) {
		item.transform(Keys.APPLIED_ENCHANTMENTS, enchantments -> {
			if (enchantments == null) {
				return Collections.emptyList();
			}
			enchantments.removeIf(enchantment -> enchantment.getType().key().asString().equalsIgnoreCase(enchant.getId()));
			return enchantments;
		});
	}

	@Override
	public Object getDefault() {
		return item;
	}
}
