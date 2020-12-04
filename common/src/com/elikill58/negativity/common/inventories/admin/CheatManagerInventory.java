package com.elikill58.negativity.common.inventories.admin;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.admin.CheatManagerHolder;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CheatManagerInventory extends AbstractInventory<CheatManagerHolder> {

	public CheatManagerInventory() {
		super(NegativityInventory.CHEAT_MANAGER, CheatManagerHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args){
		Inventory inv = Inventory.createInventory(Inventory.CHEAT_MANAGER, UniversalUtils.getMultipleOf(Cheat.values().size() + 3, 9, 1, 54), new CheatManagerHolder((boolean) args[0]));
		int slot = 0;
		for(Cheat c : Cheat.values())
			if(c.getMaterial() != null)
				inv.set(slot++, ItemBuilder.Builder(c.getMaterial()).displayName(c.getName()).build());

		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheatManagerHolder nh) {
		if (m.equals(Materials.ARROW))
			InventoryManager.open(nh.isFromAdmin() ? NegativityInventory.ADMIN : NegativityInventory.MOD, p);
		else {
			UniversalUtils.getCheatFromItem(m).ifPresent((c) -> InventoryManager.open(NegativityInventory.ONE_CHEAT, p, c));
		}
	}
}
