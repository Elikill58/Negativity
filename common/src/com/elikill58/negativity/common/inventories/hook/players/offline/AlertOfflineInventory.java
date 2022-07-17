package com.elikill58.negativity.common.inventories.hook.players.offline;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.players.offline.AlertOfflineHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AlertOfflineInventory extends AbstractInventory<AlertOfflineHolder> {

	public AlertOfflineInventory() {
		super(NegativityInventory.ALERT_OFFLINE, AlertOfflineHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		NegativityAccount account = NegativityAccount.get(cible.getUniqueId());
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			TO_SEE.add(c);
		}
		Inventory inv = Inventory.createInventory(Messages.getMessage(p, "inventory.detection.name_inv"),
				UniversalUtils.getMultipleOf(TO_SEE.size() + 3, 9, 1, 54), new AlertOfflineHolder(cible));
		int slot = 0;
		for (Cheat c : TO_SEE) {
			long warn = account.getWarn(c);
			if (c.getMaterial().getDefault() == null)
				Adapter.getAdapter().getLogger().error("Cannot find material for cheat " + c.getName());
			else
				inv.set(slot++,
						ItemBuilder.Builder(c.getMaterial())
								.displayName(Messages.getMessage(p, "inventory.alerts.item_name", "%exact_name%",
										c.getName(), "%warn%", warn))
								.amount(warn == 0 ? 1 : (warn > Integer.MAX_VALUE ? 64 : (int) warn)).build());
		}
		inv.set(inv.getSize() - 3, ItemBuilder.Builder(Materials.BONE).displayName(ChatColor.RESET + "" + ChatColor.GRAY + "Clear").build());
		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		Inventory inv = p.getOpenInventory();
		NegativityAccount account = NegativityAccount.get(cible.getUniqueId());
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			TO_SEE.add(c);
		}
		int slot = 0;
		for (Cheat c : TO_SEE) {
			long warn = account.getWarn(c);
			if (c.getMaterial().getDefault() == null)
				Adapter.getAdapter().getLogger().error("Cannot find material for cheat " + c.getName());
			else
				inv.set(slot++,
						ItemBuilder.Builder(c.getMaterial())
								.displayName(Messages.getMessage(p, "inventory.alerts.item_name", "%exact_name%",
										c.getName(), "%warn%", warn))
								.amount(warn == 0 ? 1 : (warn > Integer.MAX_VALUE ? 64 : (int) warn)).build());
		}
		p.updateInventory();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, AlertOfflineHolder nh) {
		OfflinePlayer cible = nh.getCible();
		if (m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.CHECK_MENU_OFFLINE, p, cible);
		else if (m.equals(Materials.BONE)) {
			NegativityAccount account = NegativityAccount.get(cible.getUniqueId());
			for (Cheat c : Cheat.values()) {
				account.setWarnCount(c, 0);
			}
			actualizeInventory(p, cible);
		}
	}
}
