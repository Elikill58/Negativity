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
import com.elikill58.negativity.common.inventories.holders.admin.OneSpecialHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Special;

public class OneSpecialInventory extends AbstractInventory<OneSpecialHolder> {

	public OneSpecialInventory() {
		super(NegativityInventory.ONE_SPECIAL, OneSpecialHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args){
		Special s = (Special) args[0];
		Inventory inv = Inventory.createInventory(s.getName(), 9, new OneSpecialHolder(s));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		inv.set(0, ItemBuilder.Builder(s.getMaterial()).displayName(s.getName()).build());
		actualizeInventory(p, s, inv);
		
		inv.set(7, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(8, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Special c = (Special) args[0];
		Inventory inv = (Inventory) args[1];
		inv.set(2, ItemBuilder.Builder(Materials.DIAMOND).displayName(Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))).build());
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, OneSpecialHolder nh) {
		if (m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.ADMIN_CHEAT_MANAGER, p, true);
			return;
		}
		Inventory inv = e.getClickedInventory();
		Special s = nh.getSpecial();
		if (m.equals(s.getMaterial()))
			return;

		actualizeInventory(p, s, inv);
		p.updateInventory();
	}
}
