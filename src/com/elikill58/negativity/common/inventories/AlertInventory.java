package com.elikill58.negativity.common.inventories;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.ChatColor;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.AlertHolder;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class AlertInventory extends AbstractInventory {

	public AlertInventory() {
		super(NegativityInventory.ALERT);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof AlertHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			if (!c.isActive())
				continue;
			ConfigAdapter config = Adapter.getAdapter().getConfig();
			boolean isActive = np.hasDetectionActive(c);
			if ((config.getBoolean("inventory.alerts.only_cheat_active") && isActive)
					|| (!isActive && config.getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		Inventory inv = Inventory.createInventory(Messages.getMessage(p, "inventory.detection.name_inv"),
				UniversalUtils.getMultipleOf(TO_SEE.size() + 3, 9, 1, 54), new AlertHolder(cible));
		int slot = 0;
		for (Cheat c : TO_SEE) {
			if (c.getMaterial().getDefault() == null)
				Adapter.getAdapter().getLogger().error("Cannot find material for cheat " + c.getName());
			else
				inv.set(slot++,
						ItemBuilder.Builder(c.getMaterial())
								.displayName(Messages.getMessage(p, "inventory.alerts.item_name", "%exact_name%",
										c.getName(), "%warn%", np.getWarn(c)))
								.amount(np.getWarn(c) == 0 ? 1 : np.getWarn(c)).build());
		}
		inv.set(inv.getSize() - 3, ItemBuilder.Builder(Materials.BONE).displayName(ChatColor.RESET + "" + ChatColor.GRAY + "Clear").build());
		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void actualizeInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = p.getOpenInventory();
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(cible);
		List<Cheat> TO_SEE = new ArrayList<>();
		for (Cheat c : Cheat.values()) {
			ConfigAdapter config = Adapter.getAdapter().getConfig();
			boolean isActive = np.hasDetectionActive(c);
			if ((config.getBoolean("inventory.alerts.only_cheat_active") && isActive)
					|| (!isActive && config.getBoolean("inventory.alerts.no_started_verif_cheat")))
				TO_SEE.add(c);
		}
		int slot = 0;
		for (Cheat c : TO_SEE)
			inv.set(slot++,
					ItemBuilder.Builder(c.getMaterial())
							.displayName(Messages.getMessage(p, "inventory.alerts.item_name", "%exact_name%",
									c.getName(), "%warn%", np.getWarn(c)))
							.amount(np.getWarn(c) == 0 ? 1 : np.getWarn(c)).build());
		p.updateInventory();
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		Player cible = ((AlertHolder) nh).getCible();
		if (m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.CHECK_MENU, p, cible);
		else if (m.equals(Materials.BONE)) {
			NegativityAccount account = NegativityAccount.get(cible.getUniqueId());
			for (Cheat c : Cheat.values()) {
				account.setWarnCount(c, 0);
			}
			actualizeInventory(p, cible);
		}
	}
}
