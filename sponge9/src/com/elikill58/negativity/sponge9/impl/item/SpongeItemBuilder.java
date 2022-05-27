package com.elikill58.negativity.sponge9.impl.item;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.EnchantmentType;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryTypes;

import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge9.utils.Utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class SpongeItemBuilder extends ItemBuilder {
	
	private final org.spongepowered.api.item.inventory.ItemStack item;
	
	public SpongeItemBuilder(Material type) {
		ItemType itemType = Utils.getItemType(type);
		if (itemType == null) {
			throw new IllegalArgumentException("No ItemType for material " + type);
		}
		this.item = org.spongepowered.api.item.inventory.ItemStack.of(itemType);
	}
	
	public SpongeItemBuilder(ItemStack item) {
		this.item = (org.spongepowered.api.item.inventory.ItemStack) item.getDefault();
	}
	
	public SpongeItemBuilder(GameProfile skullProfile) {
		this.item = org.spongepowered.api.item.inventory.ItemStack.builder()
			.itemType(ItemTypes.PLAYER_HEAD)
			.add(Keys.GAME_PROFILE, skullProfile)
			.build();
	}
	
	@Override
	public ItemBuilder displayName(String displayName) {
		item.offer(Keys.CUSTOM_NAME, LegacyComponentSerializer.legacyAmpersand().deserialize(displayName));
		return this;
	}
	
	@Override
	public ItemBuilder resetDisplayName() {
		item.remove(Keys.CUSTOM_NAME);
		return this;
	}
	
	@Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		Registry<EnchantmentType> registry = Sponge.game().registry(RegistryTypes.ENCHANTMENT_TYPE);
		EnchantmentType enchantmentType = registry.value(ResourceKey.resolve(enchantment.getId()));
		item.offerSingle(Keys.APPLIED_ENCHANTMENTS, org.spongepowered.api.item.enchantment.Enchantment.of(enchantmentType, level));
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
			return DyeColors.GRAY.get();
		case LIME:
			return DyeColors.LIME.get();
		case RED:
			return DyeColors.RED.get();
		case WHITE:
			return DyeColors.WHITE.get();
		case YELLOW:
			return DyeColors.YELLOW.get();
		case LIGHT_BLUE:
			return DyeColors.LIGHT_BLUE.get();
		case MAGENTA:
			return DyeColors.MAGENTA.get();
		case ORANGE:
			return DyeColors.ORANGE.get();
		case PINK:
			return DyeColors.PINK.get();
		case PURPLE:
			return DyeColors.PURPLE.get();
		}
		return DyeColors.BROWN.get();
	}
	
	@Override
	public ItemBuilder lore(List<String> lore) {
		List<Component> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(lores));
		item.offer(Keys.LORE, textLore);
		return this;
	}
	
	@Override
	public ItemBuilder lore(String... lore) {
		List<Component> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(lores));
		item.offer(Keys.LORE, textLore);
		return this;
	}
	
	@Override
	public ItemBuilder addToLore(String... loreToAdd) {
		List<Component> textLore = item.get(Keys.LORE).orElseGet(ArrayList::new);
		for (String lores : loreToAdd)
			textLore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(lores));
		item.offer(Keys.LORE, textLore);
		return this;
	}
	
	@Override
	public ItemStack build() {
		return new SpongeItemStack(item);
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
	
}
