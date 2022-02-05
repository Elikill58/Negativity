package com.elikill58.negativity.common.inventories.admin;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.admin.SpecialManagerHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpecialManagerInventory extends AbstractInventory<SpecialManagerHolder> {

	public SpecialManagerInventory() {
		super(NegativityInventory.ADMIN_SPECIAL_MANAGER, SpecialManagerHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args){
		Inventory inv = Inventory.createInventory(Inventory.SPECIAL_MANAGER, UniversalUtils.getMultipleOf(Special.values().size() + 3, 9, 1, 54), new SpecialManagerHolder());
		int slot = 0;
		for(Special c : Special.values())
			if(c.getMaterial() != null)
				inv.set(slot++, getItem(c, p));

		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	private ItemStack getItem(Special c, Player p) {
		ItemBuilder builder = ItemBuilder.Builder(c.getMaterial()).displayName(c.getName()).lore(ChatColor.GRAY
				+ "State: " + Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")));
		if (c.isActive())
			builder.unsafeEnchant(Enchantment.UNBREAKING, 1).itemFlag(ItemFlag.HIDE_ENCHANTS);
		return builder.build();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, SpecialManagerHolder nh) {
		if (m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.ADMIN, p);
		else {
			UniversalUtils.getSpecialFromItem(m).ifPresent(s -> InventoryManager.open(NegativityInventory.ONE_SPECIAL, p, s));
		}
	}
}
