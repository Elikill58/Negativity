package com.elikill58.negativity.spigot.inventories.admin;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.holders.AdminHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.InventoryUtils;
import com.elikill58.negativity.spigot.utils.ItemUtils;

public class AdminInventory extends AbstractInventory {

	public AdminInventory() {
		super(InventoryType.ADMIN);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		Inventory inv = Bukkit.createInventory(new AdminHolder(), 9, Inv.ADMIN_MENU);

		inv.setItem(0, createItem(Material.TNT, Messages.getMessage(p, "inventory.mod.cheat_manage")));
		inv.setItem(1, createItem(Material.PAPER, Messages.getMessage(p, "lang.edit")));
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		InventoryUtils.fillInventory(inv, Inv.EMPTY);
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if(m.name().contains("PAPER")) {
			AbstractInventory.open(InventoryType.LANG, p);
		} else if (m.equals(Material.TNT))
			AbstractInventory.open(InventoryType.CHEAT_MANAGER, p, true);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AdminHolder;
	}
}
