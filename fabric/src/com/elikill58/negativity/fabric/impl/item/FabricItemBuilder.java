package com.elikill58.negativity.fabric.impl.item;

import java.util.List;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.mojang.authlib.GameProfile;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack.TooltipSection;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;

public class FabricItemBuilder extends ItemBuilder {

	private final net.minecraft.item.ItemStack item;

	public FabricItemBuilder(ItemStack def) {
		this.item = (net.minecraft.item.ItemStack) def.getDefault();
	}
	
	public FabricItemBuilder(Material type) {
		this.item = new net.minecraft.item.ItemStack(((Item) type.getDefault()).asItem());
	}
	
	public FabricItemBuilder(OfflinePlayer owner) {
		this.item = new net.minecraft.item.ItemStack(Items.SKELETON_SKULL);
		item.getOrCreateNbt().put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), new GameProfile(owner.getUniqueId(), null)));
	}

	@Override
	public ItemBuilder displayName(String displayName) {
		item.setCustomName(Text.of(ChatColor.RESET + displayName));
		return this;
	}

	@Override
	public ItemBuilder resetDisplayName() {
		item.removeCustomName();
		return this;
	}

	@Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		item.addEnchantment(FabricEnchants.getFabricEnchant(enchantment), level);
		return this;
	}
	
	@Override
	public ItemBuilder itemFlag(ItemFlag... itemFlag) {
		for(ItemFlag flag : itemFlag) {
			switch (flag) {
			case HIDE_ATTRIBUTES:
				item.addHideFlag(TooltipSection.MODIFIERS);
				break;
			case HIDE_ENCHANTS:
				item.addHideFlag(TooltipSection.ENCHANTMENTS);
				break;
			case HIDE_UNBREAKABLE:
				item.addHideFlag(TooltipSection.UNBREAKABLE);
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
		item.setCount(amount);
		return this;
	}

	@Override
	public ItemBuilder color(com.elikill58.negativity.api.colors.DyeColor color) {
		// TODO implement color
		//item.offer(Keys.DYE_COLOR, getColor(color));
		return this;
	}
	
	public DyeColor getColor(com.elikill58.negativity.api.colors.DyeColor color) {
		switch (color) {
		case GRAY:
			return DyeColor.GRAY;
		case LIME:
			return DyeColor.LIME;
		case RED:
			return DyeColor.RED;
		case WHITE:
			return DyeColor.WHITE;
		case YELLOW:
			return DyeColor.YELLOW;
		case LIGHT_BLUE:
			return DyeColor.LIGHT_BLUE;
		case MAGENTA:
			return DyeColor.MAGENTA;
		case ORANGE:
			return DyeColor.ORANGE;
		case PINK:
			return DyeColor.PINK;
		case PURPLE:
			return DyeColor.PURPLE;
		}
		return DyeColor.BROWN;
	}

	@Override
	public ItemBuilder lore(List<String> lore) {
		NbtList textLore = new NbtList();
		for (String lores : lore)
			textLore.add(NbtString.of(lores));
		item.setSubNbt(net.minecraft.item.ItemStack.LORE_KEY, textLore);
		return this;
	}

	@Override
	public ItemBuilder lore(String... lore) {
		NbtList textLore = new NbtList();
		for (String lores : lore)
			textLore.add(NbtString.of(lores));
		item.setSubNbt(net.minecraft.item.ItemStack.LORE_KEY, textLore);
		return this;
	}

	@Override
	public ItemBuilder addToLore(String... loreToAdd) {
		// Fix getting lore
		return lore(loreToAdd);
	}

	@Override
	public ItemStack build() {
		return new FabricItemStack(item);
	}

}
