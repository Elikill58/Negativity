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
import com.elikill58.negativity.common.inventories.holders.admin.OneCheatHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Cheat;


public class OneCheatInventory extends AbstractInventory<OneCheatHolder> {

	public OneCheatInventory() {
		super(NegativityInventory.ONE_CHEAT, OneCheatHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args){
		Cheat c = (Cheat) args[0];
		Inventory inv = Inventory.createInventory(c.getName(), 27, new OneCheatHolder(c));
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		inv.set(0, ItemBuilder.Builder(c.getMaterial()).displayName(c.getName()).build());
		inv.set(11, ItemBuilder.Builder(Materials.DIAMOND).displayName(Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))).build());
		inv.set(12, ItemBuilder.Builder(Materials.TNT).displayName(Messages.getMessage(p, "inventory.manager.setBack.name", "%state%", Messages.getMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))).lore(Messages.getMessage(p, "inventory.manager.setBack.lore")).build());
		inv.set(13, ItemBuilder.Builder(Materials.BLAZE_ROD).displayName(Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))).build());
		inv.set(14, ItemBuilder.Builder(Materials.APPLE).displayName(Messages.getMessage(p, "inventory.manager.verif", "%verif%", Messages.getMessage(p, "inventory.manager." + (c.hasVerif() ? "enabled" : "disabled")))).build());
		
		if(!c.getChecks().isEmpty())
			inv.set(15, ItemBuilder.Builder(Materials.ENDER_CHEST).displayName("Checks").build());
		
		inv.set(8, Inventory.getBackItem(p));
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, OneCheatHolder nh) {
		if (m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.ADMIN_CHEAT_MANAGER, p, true);
			return;
		}
		Cheat c = nh.getCheat();
		if (m.equals(c.getMaterial()))
			return;
		if(m.equals(Materials.ENDER_CHEST)) {
			InventoryManager.open(NegativityInventory.CHEAT_CHECKS, p, c);
		} else {
			if(m.equals(Materials.TNT))
				c.setBack(!c.isSetBack());
			else if(m.equals(Materials.BLAZE_ROD))
				c.setAllowKick(!c.allowKick());
			else if(m.equals(Materials.DIAMOND))
				c.setActive(!c.isActive());
			else if(m.equals(Materials.APPLE))
				c.setVerif(!c.hasVerif());
			else if(m.equals(Materials.ENDER_CHEST))
				InventoryManager.open(NegativityInventory.CHEAT_CHECKS, p, c);
			
			c.saveConfig();
			openInventory(p, c);
		}
	}
}
