package com.elikill58.negativity.minestom.impl.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;

import net.kyori.adventure.text.Component;
import net.minestom.server.component.DataComponent;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack.Builder;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.item.component.Unbreakable;
import net.minestom.server.registry.DynamicRegistry;

public class MinestomItemBuilder extends ItemBuilder {

	private final Builder item;

	public MinestomItemBuilder(ItemStack def) {
		net.minestom.server.item.ItemStack i = (net.minestom.server.item.ItemStack) def.getDefault();
		this.item = net.minestom.server.item.ItemStack.builder(i.material());
		for(DataComponent dc : ItemComponent.values()) {
			Object obj = i.get(dc);
			if(obj != null)
				this.item.set(dc, obj);
		}
	}
	
	public MinestomItemBuilder(Material type) {
		this.item = net.minestom.server.item.ItemStack.builder((net.minestom.server.item.Material) type.getDefault());
	}
	
	public MinestomItemBuilder(OfflinePlayer owner) {
		this.item = net.minestom.server.item.ItemStack.builder(net.minestom.server.item.Material.PLAYER_HEAD);
		this.item.set(ItemComponent.PROFILE, new HeadProfile(owner.getName(), owner.getUniqueId(), Collections.emptyList()));
	}

	@Override
	public ItemBuilder displayName(String displayName) {
		item.set(ItemComponent.ITEM_NAME, Component.text(ChatColor.WHITE + displayName));
		return this;
	}

	@Override
	public ItemBuilder resetDisplayName() {
		item.set(ItemComponent.ITEM_NAME, Component.empty());
		return this;
	}

	public ItemBuilder unenchant(Enchantment enchantment) {
		// not managed yet
		return this;
	}

	@Override
	public ItemBuilder enchant(Enchantment enchantment, int level) {
		Map<DynamicRegistry.Key<net.minestom.server.item.enchant.Enchantment>, Integer> enchantments = new HashMap<>();
		enchantments.put(MinestomEnchants.getEnchant(enchantment), level);
		item.set(ItemComponent.ENCHANTMENTS, new EnchantmentList(enchantments, false));
		return this;
	}
	
	@Override
	public ItemBuilder itemFlag(ItemFlag... itemFlag) {
		for(ItemFlag flag : itemFlag) {
			switch (flag) {
			case HIDE_ENCHANTS:
				item.set(ItemComponent.HIDE_TOOLTIP, null);
				break;
			case HIDE_ATTRIBUTES:
				item.set(ItemComponent.HIDE_ADDITIONAL_TOOLTIP, null);
				break;
			case HIDE_UNBREAKABLE:
				item.set(ItemComponent.UNBREAKABLE, Unbreakable.DEFAULT);
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
		item.amount(amount);
		return this;
	}

	@Override
	public ItemBuilder color(com.elikill58.negativity.api.colors.DyeColor color) {
		item.set(ItemComponent.DAMAGE, (int) color.getDye());
		return this;
	}

	@Override
	public ItemBuilder lore(List<String> lore) {
		List<Component> result = new ArrayList<>();
		for(String line : lore)
			for(String part : line.split("\n"))
				result.add(Component.text(part));
		item.set(ItemComponent.LORE, result);
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
