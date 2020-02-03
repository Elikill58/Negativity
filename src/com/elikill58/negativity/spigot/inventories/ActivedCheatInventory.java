package com.elikill58.negativity.spigot.inventories;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Inv;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class ActivedCheatInventory {

	public static void openActivedCheat(Player p, Player cible) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(np.ACTIVE_CHEAT.size() + 3, 9, 1), Inv.NAME_ACTIVED_CHEAT_MENU);
		if (np.ACTIVE_CHEAT.size() > 0) {
			int slot = 0;
			for (Cheat c : np.ACTIVE_CHEAT)
				inv.setItem(slot++, Utils.createItem((Material) c.getMaterial(), ChatColor.RESET + c.getName()));
		} else
			inv.setItem(4, Utils.createItem(Material.REDSTONE_BLOCK, Messages.getMessage(p, "inventory.detection.no_active", "%name%", cible.getName())));
		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
	
	public static void manageActivedCheatMenu(InventoryClickEvent e, Material m, Player p) {
		e.setCancelled(true);
		if (m.equals(SpigotNegativity.MATERIAL_CLOSE)) {
			p.closeInventory();
		} else if (m.equals(Material.ARROW))
			CheckMenuInventory.openCheckMenu(p, Inv.CHECKING.get(p));
	}
}
