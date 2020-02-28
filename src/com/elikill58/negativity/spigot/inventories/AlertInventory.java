package com.elikill58.negativity.spigot.inventories;

import java.util.ArrayList;
import java.util.List;

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
import com.elikill58.negativity.universal.adapter.Adapter;

public class AlertInventory {

	public static void openAlertMenu(Player p, Player cible) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			if((Adapter.getAdapter().getBooleanInConfig("inventory.alerts.only_cheat_active") && np.ACTIVE_CHEAT.contains(c))
					|| (!np.ACTIVE_CHEAT.contains(c) && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		Inventory inv = Bukkit.createInventory(null, Utils.getMultipleOf(TO_SEE.size() + 3, 9, 1), Inv.NAME_ALERT_MENU);
		int slot = 0;
		for (Cheat c : TO_SEE) {
			if (c.getMaterial() != null){
				inv.setItem(slot++, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c))));
			}
		}
		inv.setItem(inv.getSize() - 3, Utils.createItem(Material.BONE, ChatColor.RESET + "" + ChatColor.GRAY + "Clear"));
		inv.setItem(inv.getSize() - 2, Utils.createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1,
				Utils.createItem(SpigotNegativity.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}

	public static void actualizeAlertMenu(Player p, Player cible) {
		Inventory inv = p.getOpenInventory().getTopInventory();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if ((c.isActive()
					&& Adapter.getAdapter().getBooleanInConfig("inventory.alerts.only_cheat_active") && np.ACTIVE_CHEAT.contains(c))
					|| (!np.ACTIVE_CHEAT.contains(c) && Adapter.getAdapter().getBooleanInConfig("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		int slot = 0;
		for (Cheat c : TO_SEE)
			if (c.getMaterial() != null)
				inv.setItem(slot++, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c))));
		//p.updateInventory();
	}
	
	public static void manageAlertMenu(InventoryClickEvent e, Material m, Player p) {
		e.setCancelled(true);
		if (m.equals(SpigotNegativity.MATERIAL_CLOSE))
			p.closeInventory();
		else if (m.equals(Material.ARROW))
			CheckMenuInventory.openCheckMenu(p, Inv.CHECKING.get(p));
		else if (m.equals(Material.BONE)) {
			for (Cheat c : Cheat.values())
				SpigotNegativityPlayer.getNegativityPlayer(Inv.CHECKING.get(p)).setWarn(c, 0);
			actualizeAlertMenu(p, Inv.CHECKING.get(p));
		}
	}
}
