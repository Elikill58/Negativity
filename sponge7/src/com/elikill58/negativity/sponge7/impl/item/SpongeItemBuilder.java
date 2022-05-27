package com.elikill58.negativity.sponge7.impl.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;

public class SpongeItemBuilder extends ItemBuilder {

	private final org.spongepowered.api.item.inventory.ItemStack item;

	public SpongeItemBuilder(ItemStack def) {
		this.item = (org.spongepowered.api.item.inventory.ItemStack) def.getDefault();
	}
	
	public SpongeItemBuilder(Material type) {
		Object spongeMaterial = type.getDefault();
		if (spongeMaterial instanceof ItemType) {
			this.item = org.spongepowered.api.item.inventory.ItemStack.of((ItemType) spongeMaterial);
		} else {
			throw new IllegalArgumentException("Material " + type.getId() + " does not have a corresponding item");
		}
	}
	
	public SpongeItemBuilder(com.elikill58.negativity.api.entity.OfflinePlayer owner) {
		this.item = org.spongepowered.api.item.inventory.ItemStack.builder()
				.itemType(ItemTypes.SKULL)
				.add(Keys.REPRESENTED_PLAYER, ((User) owner.getDefault()).getProfile())
				.add(Keys.SKULL_TYPE, SkullTypes.PLAYER).build();
	}

	@Override
	public ItemBuilder displayName(String displayName) {
		item.offer(Keys.DISPLAY_NAME, Text.of(ChatColor.RESET + displayName));
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
	public ItemBuilder itemFlag(ItemFlag... itemFlag) {
		for(ItemFlag flag : itemFlag) {
			switch (flag) {
			case HIDE_ATTRIBUTES:
				item.offer(Keys.HIDE_ATTRIBUTES, true);
				break;
			case HIDE_ENCHANTS:
				item.offer(Keys.HIDE_ENCHANTMENTS, true);
				break;
			case HIDE_UNBREAKABLE:
				item.offer(Keys.HIDE_UNBREAKABLE, true);
				break;
			}
		}
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
	public ItemBuilder color(com.elikill58.negativity.api.colors.DyeColor color) {
		item.offer(Keys.DYE_COLOR, getColor(color));
		return this;
	}
	
	private DyeColor getColor(com.elikill58.negativity.api.colors.DyeColor color) {
		switch (color) {
		case GRAY:
			return DyeColors.GRAY;
		case LIME:
			return DyeColors.LIME;
		case RED:
			return DyeColors.RED;
		case WHITE:
			return DyeColors.WHITE;
		case YELLOW:
			return DyeColors.YELLOW;
		case LIGHT_BLUE:
			return DyeColors.LIGHT_BLUE;
		case MAGENTA:
			return DyeColors.MAGENTA;
		case ORANGE:
			return DyeColors.ORANGE;
		case PINK:
			return DyeColors.PINK;
		case PURPLE:
			return DyeColors.PURPLE;
		}
		return DyeColors.BROWN;
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
