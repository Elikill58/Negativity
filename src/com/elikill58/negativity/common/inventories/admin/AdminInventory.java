package com.elikill58.negativity.common.inventories.admin;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.admin.AdminHolder;
import com.elikill58.negativity.universal.Messages;

public class AdminInventory extends AbstractInventory<AdminHolder> {

	public AdminInventory() {
		super(NegativityInventory.ADMIN, AdminHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.createInventory(Inventory.ADMIN_MENU, 9, new AdminHolder());

		inv.set(0, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.mod.cheat_manage")).build());
		inv.set(1, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "lang.edit")).build());
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, AdminHolder nh) {
		if(m.equals(Materials.PAPER)) {
			InventoryManager.open(NegativityInventory.LANG, p);
		} else if (m.equals(Materials.TNT))
			InventoryManager.open(NegativityInventory.CHEAT_MANAGER, p, true);
	}
}
