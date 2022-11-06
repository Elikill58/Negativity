package com.elikill58.negativity.minestom.impl.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack.Builder;
import net.minestom.server.item.metadata.PlayerHeadMeta;

public class MinestomItemBuilder extends ItemBuilder {

	private final Builder item;

	public MinestomItemBuilder(ItemStack def) {
		net.minestom.server.item.ItemStack i = (net.minestom.server.item.ItemStack) def.getDefault();
		this.item = net.minestom.server.item.ItemStack.builder(i.material());
		this.item.meta(i.meta());
	}
	
	public MinestomItemBuilder(Material type) {
		this.item = net.minestom.server.item.ItemStack.builder((net.minestom.server.item.Material) type.getDefault());
	}
	
	public MinestomItemBuilder(OfflinePlayer owner) {
		this.item = net.minestom.server.item.ItemStack.builder(net.minestom.server.item.Material.PLAYER_HEAD);
		this.item.meta(new PlayerHeadMeta.Builder().skullOwner(owner.getUniqueId()).build());
	}

	@Override
	public ItemBuilder displayName(String displayName) {
		item.displayName(Component.text(ChatColor.WHITE + displayName));
		return this;
	}

	@Override
	public ItemBuilder resetDisplayName() {
		item.displayName(Component.empty());
		return this;
	}

	public ItemBuilder unenchant(Enchantment enchantment) {
		item.meta(build -> build.enchantment(net.minestom.server.item.Enchantment.fromNamespaceId(enchantment.getId()), (short) 0));
		return this;
	}

	@Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		item.meta(build -> build.enchantment(net.minestom.server.item.Enchantment.fromNamespaceId(enchantment.getId()), (short) level));
		return this;
	}
	
	@Override
	public ItemBuilder itemFlag(ItemFlag... itemFlag) {
		ItemHideFlag[] flag = new ItemHideFlag[itemFlag.length];
		for(int i = 0; i < itemFlag.length; i++)
			flag[i] = ItemHideFlag.valueOf(itemFlag[i].name());
		item.meta(build -> build.hideFlag(flag));
		return this;
	}

	@Override
	public ItemBuilder unsafeEnchant(Enchantment enchantment, int level) {
		return enchant(enchantment, level);
	}

	@Override
	public ItemBuilder amount(int amount) {
		item.amount(amount);
		return this;
	}

	@Override
	public ItemBuilder color(com.elikill58.negativity.api.colors.DyeColor color) {
		item.meta(build -> build.damage(color.getDye()));
		return this;
	}

	@Override
	public ItemBuilder lore(List<String> lore) {
		List<Component> result = new ArrayList<>();
		for(String line : lore)
			for(String part : line.split("\n"))
				result.add(Component.text(part));
		item.lore(result);
		return this;
	}

	@Override
	public ItemBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	@Override
	public ItemBuilder addToLore(String... loreToAdd) {
		return lore(Arrays.asList(loreToAdd));
	}

	@Override
	public ItemStack build() {
		return new MinestomItemStack(item.build());
	}

}
