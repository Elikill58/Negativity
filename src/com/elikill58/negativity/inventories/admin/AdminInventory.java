package com.elikill58.negativity.inventories.admin;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.common.inventory.AbstractInventory;
import com.elikill58.negativity.common.inventory.Inventory;
import com.elikill58.negativity.common.inventory.InventoryManager;
import com.elikill58.negativity.common.inventory.NegativityHolder;
import com.elikill58.negativity.common.item.ItemBuilder;
import com.elikill58.negativity.common.item.Material;
import com.elikill58.negativity.common.item.Materials;
import com.elikill58.negativity.common.utils.InventoryUtils;
import com.elikill58.negativity.inventories.holders.admin.AdminHolder;
import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.universal.Messages;

public class AdminInventory extends AbstractInventory {

	public AdminInventory() {
		super(NegativityInventory.ADMIN);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AdminHolder;
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Inventory.createInventory(Inv.ADMIN_MENU, 9, new AdminHolder());

		inv.set(0, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.mod.cheat_manage")).build());
		inv.set(1, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "lang.edit")).build());
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		InventoryUtils.fillInventory(inv, Inv.EMPTY);
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if(m.getId().contains("PAPER")) {
			InventoryManager.open(NegativityInventory.LANG, p);
		} else if (m.equals(Materials.TNT))
			InventoryManager.open(NegativityInventory.CHEAT_MANAGER, p, true);
	}
}
