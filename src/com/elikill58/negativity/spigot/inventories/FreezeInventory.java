package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.FreezeHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;

public class FreezeInventory extends AbstractInventory {

	public FreezeInventory() {
		super(InventoryType.FREEZE);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof FreezeHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Bukkit.createInventory(new FreezeHolder(), 9, Inv.NAME_FREEZE_MENU);
		inv.setItem(4, ItemUtils.createItem(Material.PAPER, Messages.getMessage(p, "inventory.mod.you_are_freeze")));
		p.openInventory(inv);
	}
	
	@Override
	public void closeInventory(Player p, InventoryCloseEvent e) {
		if(SpigotNegativityPlayer.getNegativityPlayer(p).isFreeze)
			openInventory(p);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		
	}
}
