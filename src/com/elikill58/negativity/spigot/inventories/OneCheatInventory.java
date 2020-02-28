package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class OneCheatInventory {

	public static void openOneCheatMenu(Player p, Cheat c){
		Inventory inv = Bukkit.createInventory(null, 27, c.getName());
		inv.setItem(2, Utils.createItem(Material.TNT, Messages.getMessage(p, "inventory.manager.setBack", "%back%", Messages.getMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))));
		inv.setItem(5, Utils.createItem(Utils.getMaterialWith1_15_Compatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER"), Messages.getMessage(p, "inventory.manager.autoVerif", "%auto%", Messages.getMessage(p, "inventory.manager." + (c.isAutoVerif() ? "enabled" : "disabled")))));
		inv.setItem(9, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), c.getName())));
		inv.setItem(20, Utils.createItem(Material.BLAZE_ROD, Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))));
		inv.setItem(23, Utils.createItem(Material.DIAMOND, Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))));
		
		inv.setItem(8, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, Inv.EMPTY);
		p.openInventory(inv);
	}
	
	public static void manageOneCheatMenu(InventoryClickEvent e, Material m, Player p) {

		e.setCancelled(true);
		if (m.equals(SpigotNegativity.MATERIAL_CLOSE)) {
			p.closeInventory();
			return;
		} else if (m.equals(Material.ARROW)) {
			CheatManagerInventory.openCheatManagerMenu(p);
			return;
		}
		Inventory inv = e.getClickedInventory();
		Cheat c = Utils.getCheatFromName(Utils.getInventoryName(e)).get();
		if (m.equals(c.getMaterial()))
			return;
		int slot = e.getRawSlot();
		if (m.equals(Material.TNT))
			inv.setItem(slot,
					Utils.createItem(m,
							Messages.getMessage(p, "inventory.manager.setBack", "%back%", Messages.getMessage(p,
									"inventory.manager." + (c.setBack(!c.isSetBack()) ? "enabled" : "disabled")))));
		else if (m.equals(Utils.getMaterialWith1_15_Compatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER")))
			inv.setItem(slot, Utils.createItem(m,
					Messages.getMessage(p, "inventory.manager.autoVerif", "%auto%", Messages.getMessage(p,
							"inventory.manager." + (c.setAutoVerif(!c.isAutoVerif()) ? "enabled" : "disabled")))));
		else if (m.equals(Material.BLAZE_ROD))
			inv.setItem(slot, Utils.createItem(m,
					Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p,
							"inventory.manager." + (c.setAllowKick(!c.allowKick()) ? "enabled" : "disabled")))));
		else if (m.equals(Material.DIAMOND))
			inv.setItem(slot, Utils.createItem(m,
					Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p,
							"inventory.manager." + (c.setActive(!c.isActive()) ? "enabled" : "disabled")))));
		p.updateInventory();
	}
}
