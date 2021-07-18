package com.elikill58.negativity.spigot.inventories;

import static com.elikill58.negativity.spigot.utils.ItemUtils.createItem;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.inventories.holders.AlertHolder;
import com.elikill58.negativity.spigot.inventories.holders.NegativityHolder;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AlertInventory extends AbstractInventory {

	public AlertInventory() {
		super(InventoryType.ALERT);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AlertHolder;
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			ConfigAdapter config = Adapter.getAdapter().getConfig();
			boolean isActive = np.hasDetectionActive(c) || !cible.isOnline();
			if((config.getBoolean("inventory.alerts.only_cheat_active") && isActive)
					|| (!isActive && config.getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		Inventory inv = Bukkit.createInventory(new AlertHolder(cible), UniversalUtils.getMultipleOf(TO_SEE.size() + 3, 9, 1, 54),
				Messages.getMessage(p, "inventory.detection.name_inv"));
		int slot = 0;
		for (Cheat c : TO_SEE) {
			if (c.getMaterial() != null){
				inv.setItem(slot++, ItemUtils.hideAttributes(createItem((Material) c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c))));
			}
		}
		inv.setItem(inv.getSize() - 3, createItem(Material.BONE, ChatColor.RESET + "" + ChatColor.GRAY + "Clear"));
		inv.setItem(inv.getSize() - 2, createItem(Material.ARROW, Messages.getMessage(p, "inventory.back")));
		inv.setItem(inv.getSize() - 1, createItem(ItemUtils.MATERIAL_CLOSE, Messages.getMessage(p, "inventory.close")));
		p.openInventory(inv);
	}
	
	@Override
	public void actualizeInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		Inventory inv = p.getOpenInventory().getTopInventory();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			ConfigAdapter config = Adapter.getAdapter().getConfig();
			boolean isActive = np.hasDetectionActive(c) || !cible.isOnline();
			if((config.getBoolean("inventory.alerts.only_cheat_active") && isActive)
					|| (!isActive && config.getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		int slot = 0;
		for (Cheat c : TO_SEE)
			if (c.getMaterial() != null)
				inv.setItem(slot++, ItemUtils.hideAttributes(createItem((Material) c.getMaterial(), Messages.getMessage(p, "inventory.alerts.item_name",
						"%exact_name%", c.getName(), "%warn%", String.valueOf(np.getWarn(c))), np.getWarn(c) == 0 ? 1 : np.getWarn(c))));
		p.updateInventory();
	}
	

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		AlertHolder ah = (AlertHolder) nh;
		OfflinePlayer op = ah.getCible();
		if (m.equals(Material.ARROW))
			AbstractInventory.open(op instanceof Player ? InventoryType.CHECK_MENU : InventoryType.CHECK_MENU_OFFLINE, p, op);
		else if (m.equals(Material.BONE)) {
			NegativityAccount account = NegativityAccount.get(op.getUniqueId());
			for (Cheat c : Cheat.values()) {
				account.setWarnCount(c, 0);
			}
			actualizeInventory(p, op);
		}
	}
}
