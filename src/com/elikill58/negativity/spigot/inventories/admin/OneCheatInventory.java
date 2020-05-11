package com.elikill58.negativity.spigot.inventories.admin;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.inventories.holders.OneCheatHolder;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.adapter.Adapter;

public class OneCheatInventory extends AbstractInventory {

	public OneCheatInventory() {
		super(InventoryType.ONE_CHEAT);
	}

	@Override
	public void openInventory(Player p, Object... args){
		Cheat c = (Cheat) args[0];
		Inventory inv = Bukkit.createInventory(new OneCheatHolder(c), 9, c.getName());
		inv.setItem(0, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), c.getName())));
		actualizeInventory(p, c, inv);
		
		inv.setItem(7, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(8, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		Utils.fillInventory(inv, Inv.EMPTY);
		p.openInventory(inv);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Cheat c = (Cheat) args[0];
		Inventory inv = (Inventory) args[1];
		inv.setItem(2, Utils.createItem(Material.DIAMOND, Messages.getMessage(p, "inventory.manager.setActive", "%active%", Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled")))));
		inv.setItem(3, Utils.createItem(Utils.getMaterialWith1_15_Compatibility("EYE_OF_ENDER", "LEGACY_EYE_OF_ENDER"), Messages.getMessage(p, "inventory.manager.autoVerif", "%auto%", Messages.getMessage(p, "inventory.manager." + (c.isAutoVerif() ? "enabled" : "disabled")))));
		inv.setItem(4, Utils.createItem(Material.TNT, Messages.getMessage(p, "inventory.manager.setBack", "%back%", Messages.getMessage(p, "inventory.manager." + (c.isSetBack() ? "enabled" : "disabled")))));
		inv.setItem(5, Utils.createItem(Material.BLAZE_ROD, Messages.getMessage(p, "inventory.manager.allowKick", "%allow%", Messages.getMessage(p, "inventory.manager." + (c.allowKick() ? "enabled" : "disabled")))));
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Material.ARROW)) {
			AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, false);
			return;
		}
		Inventory inv = e.getClickedInventory();
		Cheat c = ((OneCheatHolder) nh).getCheat();
		if (m.equals(c.getMaterial()))
			return;
		if(m.equals(Material.TNT))
			c.setBack(!c.isSetBack());
		else if(m.name().contains("EYE_OF_ENDER"))
			c.setAutoVerif(!c.isAutoVerif());
		else if(m.equals(Material.BLAZE_ROD))
			c.setAllowKick(!c.allowKick());
		else if(m.equals(Material.DIAMOND))
			c.setActive(!c.isActive());

		try {
			Adapter.getAdapter().getConfig().save();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		actualizeInventory(p, c, inv);
		p.updateInventory();
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof OneCheatHolder;
	}
}
