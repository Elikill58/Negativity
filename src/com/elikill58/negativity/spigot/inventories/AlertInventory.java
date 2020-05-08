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
import com.elikill58.negativity.spigot.inventories.holders.AlertHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;

public class AlertInventory extends AbstractInventory {

	public AlertInventory() {
		super(InventoryType.ALERT);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			if((Adapter.getAdapter().getConfig().getBoolean("inventory.alerts.only_cheat_active") && np.ACTIVE_CHEAT.contains(c))
					|| (!np.ACTIVE_CHEAT.contains(c) && Adapter.getAdapter().getConfig().getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		Inventory inv = Bukkit.createInventory(new AlertHolder(), Utils.getMultipleOf(TO_SEE.size() + 3, 9, 1, 54),
				Messages.getMessage(p, "inventory.detection.name_inv"));
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
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = p.getOpenInventory().getTopInventory();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values())
			if ((c.isActive()
					&& Adapter.getAdapter().getConfig().getBoolean("inventory.alerts.only_cheat_active") && np.ACTIVE_CHEAT.contains(c))
					|| (!np.ACTIVE_CHEAT.contains(c) && Adapter.getAdapter().getConfig().getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		int slot = 0;
		for (Cheat c : TO_SEE)
			if (c.getMaterial() != null)
				inv.setItem(slot++, Utils.hideAttributes(Utils.createItem((Material) c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c))));
		p.updateInventory();
	}
	

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		if (m.equals(Material.ARROW))
			AbstractInventory.open(InventoryType.CHECK_MENU, p, Inv.CHECKING.get(p));
		else if (m.equals(Material.BONE)) {
			Player target = Inv.CHECKING.get(p);
			NegativityAccount account = NegativityAccount.get(target.getUniqueId());
			for (Cheat c : Cheat.values()) {
				account.setWarnCount(c, 0);
			}
			actualizeInventory(p, Inv.CHECKING.get(p));
		}
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AlertHolder;
	}
}
