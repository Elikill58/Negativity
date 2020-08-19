package com.elikill58.negativity.sponge.impl.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;

public class SpongeItemBuilder extends ItemBuilder {

	private final org.spongepowered.api.item.inventory.ItemStack item;

	public SpongeItemBuilder(Material type) {
		this.item = org.spongepowered.api.item.inventory.ItemStack.of((ItemType) type.getDefault());
	}

	@Override
	public ItemBuilder displayName(String displayName) {
		item.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(displayName));
		return this;
	}

	@Override
	public ItemBuilder resetDisplayName() {
		item.remove(Keys.DISPLAY_NAME);
		return this;
	}

	@Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		item.offer(Keys.ITEM_ENCHANTMENTS, Arrays.asList(org.spongepowered.api.item.enchantment.Enchantment
				.of(Sponge.getRegistry().getType(EnchantmentType.class, enchantment.name()).get(), level)));
		return this;
	}

	@Override
	public ItemBuilder unsafeEnchant(Enchantment enchantment, int level) {
		return enchant(enchantment, level);
	}

	@Override
	public ItemBuilder amount(int amount) {
		item.setQuantity(amount);
		return this;
	}

	@Override
	public ItemBuilder durability(short durability) {
		// TODO implement durability
		return this;
	}

	@Override
	public ItemBuilder lore(List<String> lore) {
		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		item.offer(Keys.ITEM_LORE, textLore);
		return this;
	}

	@Override
	public ItemBuilder lore(String... lore) {
		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		item.offer(Keys.ITEM_LORE, textLore);
		return this;
	}

	@Override
	public ItemBuilder addToLore(String... loreToAdd) {
		List<Text> textLore = item.get(Keys.ITEM_LORE).orElseGet(() -> new ArrayList<>());
		for (String lores : loreToAdd)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		item.offer(Keys.ITEM_LORE, textLore);
		return this;
	}

	@Override
	public ItemStack build() {
		return new SpongeItemStack(item);
	}

}
