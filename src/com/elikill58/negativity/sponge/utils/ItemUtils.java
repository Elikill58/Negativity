package com.elikill58.negativity.sponge.utils;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

public class ItemUtils {


	public static ItemStack createItem(ItemType m, String name, String... lore) {
		return createItem(m, name, 1, lore);
	}

	public static ItemStack createItem(ItemType m, String name, int amount, String... lore) {
		ItemStack item = ItemStack.of(m, Math.max(amount, 1));
		item.offer(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(name));
		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		item.offer(Keys.ITEM_LORE, textLore);
		return item;
	}

	public static ItemStack createItem(ItemType m, String name, int amount, DyeColor color, String... lore) {
		ItemStack item = createItem(m, name, amount, lore);
		item.offer(Keys.DYE_COLOR, color);
		return item;
	}

	public static ItemStack createSkull(String name, int amount, User owner, String... lore) {
		ItemStack skull = ItemStack.builder()
				.itemType(ItemTypes.SKULL)
				.add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
				.add(Keys.DISPLAY_NAME, TextSerializers.FORMATTING_CODE.deserialize(name))
				.add(Keys.REPRESENTED_PLAYER, owner.getProfile())
				.quantity(Math.max(amount, 1))
				.build();

		List<Text> textLore = new ArrayList<>();
		for (String lores : lore)
			textLore.add(TextSerializers.FORMATTING_CODE.deserialize(lores));
		skull.offer(Keys.ITEM_LORE, textLore);
		return skull;
	}
	
	public static ItemStack hideAttributes(ItemStack stack) {
		stack.offer(Keys.HIDE_ATTRIBUTES, true);
		stack.offer(Keys.HIDE_CAN_DESTROY, true);
		stack.offer(Keys.HIDE_CAN_PLACE, true);
		stack.offer(Keys.HIDE_ENCHANTMENTS, true);
		stack.offer(Keys.HIDE_UNBREAKABLE, true);
		stack.offer(Keys.HIDE_MISCELLANEOUS, true);
		return stack;
	}
}
