package com.elikill58.negativity.spigot.inventories.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.holders.CheatManagerHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CheatManagerInventory extends AbstractInventory {

	public CheatManagerInventory() {
		super(InventoryType.CHEAT_MANAGER);
	}
	
	@Override
	public void openInventory(Player p, Object... args){
		Inventory inv = Bukkit.createInventory(new CheatManagerHolder((boolean) args[0]), Utils.getMultipleOf(Cheat.values().size() + 3, 9, 1, 54), Inv.CHEAT_MANAGER);
		int slot = 0;
		for(Cheat c : Cheat.values())
			if(c.getMaterial() != null)
				inv.setItem(slot++, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), c.getName())));

		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Material.ARROW))
			AbstractInventory.getInventory(((CheatManagerHolder) nh).isFromAdmin() ? InventoryType.ADMIN : InventoryType.MOD).ifPresent((inv) -> inv.openInventory(p));
		else {
			UniversalUtils.getCheatFromItem(m).ifPresent((c) -> AbstractInventory.open(InventoryType.ONE_CHEAT, p, c));
		}
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof CheatManagerHolder;
	}
}
