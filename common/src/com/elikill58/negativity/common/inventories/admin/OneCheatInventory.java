package com.elikill58.negativity.common.inventories.admin;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.admin.OneCheatHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;


public class OneCheatInventory extends AbstractInventory<OneCheatHolder> {

	public OneCheatInventory() {
		super(NegativityInventory.ONE_CHEAT, OneCheatHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args){
		Cheat c = (Cheat) args[0];
		Inventory inv = Inventory.createInventory(c.getName(), 9, new OneCheatHolder(c));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		inv.set(0, ItemBuilder.Builder(c.getMaterial()).displayName(c.getName()).build());
		actualizeInventory(p, c, inv);
		
		inv.set(7, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(8, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		p.openInventory(inv);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Cheat c = (Cheat) args[0];
		Inventory inv = (Inventory) args[1];
		inv.set(2, ItemBuilder.Builder(Materials.DIAMOND).displayName(Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))).build());
		inv.set(3, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.manager.setBack", "%back%", Messages.getMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))).build());
		inv.set(4, ItemBuilder.Builder(Materials.BLAZE_ROD).displayName(Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))).build());
		inv.set(5, ItemBuilder.Builder(Materials.APPLE).displayName(Messages.getMessage(p, "inventory.manager.verif", "%verif%", Messages.getMessage(p, "inventory.manager." + (c.hasVerif() ? "enabled" : "disabled")))).build());
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, OneCheatHolder nh) {
		if (m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.CHEAT_MANAGER, p, false);
			return;
		}
		Inventory inv = e.getClickedInventory();
		Cheat c = ((OneCheatHolder) nh).getCheat();
		if (m.equals(c.getMaterial()))
			return;
		if(m.equals(Materials.TNT))
			c.setBack(!c.isSetBack());
		else if(m.equals(Materials.BLAZE_ROD))
			c.setAllowKick(!c.allowKick());
		else if(m.equals(Materials.DIAMOND))
			c.setActive(!c.isActive());
		else if(m.equals(Materials.APPLE))
			c.setVerif(!c.hasVerif());
		
		Adapter.getAdapter().getConfig().save();
		actualizeInventory(p, c, inv);
		p.updateInventory();
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof OneCheatHolder;
	}
}
