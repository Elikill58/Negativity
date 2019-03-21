package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;

public class ForgeModsInventory {

	public static int slot = 0;
	
	public static void openForgeModsMenu(Player p) {
		slot = 0;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(np.MODS.size() + 1, 9, 1), Inv.NAME_FORGE_MOD_MENU);
		if(np.MODS.size() == 0) {
			inv.setItem(4, Utils.createItem(Material.DIAMOND, "No mods"));
			inv.setItem(inv.getSize() - 1, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		} else {
			np.MODS.forEach((name, version) -> {
				inv.setItem(slot++, Utils.createItem(Material.GRASS, name, ChatColor.GRAY + "Version: " + version));
			});
		}
		for (int i = 0; i < inv.getSize(); i++)
			if (inv.getItem(i) == null)
				inv.setItem(i, Inv.EMPTY);
		p.openInventory(inv);
	}
	
	public static void manageForgeModsMenu(InventoryClickEvent e, Material m, Player p) {
		e.setCancelled(true);
		if(m.equals(Material.ARROW))
			CheckMenuInventory.openCheckMenu(p, Inv.CHECKING.get(p));
	}
}
