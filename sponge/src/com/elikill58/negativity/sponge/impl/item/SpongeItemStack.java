package com.elikill58.negativity.sponge.impl.item;

import java.util.Collections;
import java.util.List;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge.utils.Utils;

import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class SpongeItemStack extends com.elikill58.negativity.api.item.ItemStack {
	
	private final ItemStack item;
	
	public SpongeItemStack(ItemStack item) {
		this.item = item;
	}

	@Override
	public int getAmount() {
		return item.quantity();
	}

	@Override
	public Material getType() {
		ResourceKey key = Utils.getKey(item.type());
		return SpongeItemRegistrar.getInstance().get(key.asString(), key.value());
	}

	@Override
	public String getName() {
		return item.get(Keys.DISPLAY_NAME)
			.map(component -> PlainTextComponentSerializer.plainText().serialize(component))
			.orElse(null);
	}

	@Override
	public boolean hasEnchant(Enchantment enchant) {
		List<org.spongepowered.api.item.enchantment.Enchantment> enchantments = item.getOrNull(Keys.APPLIED_ENCHANTMENTS);
		if (enchantments == null) {
			return false;
		}
		for (org.spongepowered.api.item.enchantment.Enchantment enchantment : enchantments) {
			if (Utils.getKey(enchantment.type()).asString().equalsIgnoreCase(enchant.getId())) {
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
			if (Utils.getKey(enchantment.type()).asString().equalsIgnoreCase(enchant.getId())) {
				return enchantment.level();
			}
		}
		return 0;
	}

	@Override
	public void addEnchant(Enchantment enchant, int level) {
		item.offerSingle(Keys.APPLIED_ENCHANTMENTS, org.spongepowered.api.item.enchantment.Enchantment.of(getEnchantType(enchant), level));
	}
	
	private EnchantmentType getEnchantType(Enchantment enchant) {
		return Sponge.game().registry(RegistryTypes.ENCHANTMENT_TYPE).value(ResourceKey.resolve(enchant.getId()));
	}

	@Override
	public void removeEnchant(Enchantment enchant) {
		item.transform(Keys.APPLIED_ENCHANTMENTS, enchantments -> {
			if (enchantments == null) {
				return Collections.emptyList();
			}
			enchantments.removeIf(enchantment -> Utils.getKey(enchantment.type()).asString().equalsIgnoreCase(enchant.getId()));
			return enchantments;
		});
	}

	@Override
	public Object getDefault() {
		return item;
	}

	@Override
	public com.elikill58.negativity.api.item.ItemStack clone() {
		return new SpongeItemStack(item.copy());
	}
}
