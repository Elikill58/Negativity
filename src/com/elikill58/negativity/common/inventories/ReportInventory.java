package com.elikill58.negativity.common.inventories;

import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.ReportHolder;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Report;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ReportInventory extends AbstractInventory {
	
	public ReportInventory() {
		super(NegativityInventory.REPORT);
	}

	@Override
	public boolean isInstance(NegativityHolder nh) {
		return nh instanceof ReportHolder;
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		NegativityAccount na = NegativityAccount.get(cible.getUniqueId());
		List<Report> reports = na.getReports();
		Inventory inv = Inventory.createInventory("Reports", UniversalUtils.getMultipleOf(reports.size(), 9, 1, 54), new ReportHolder(cible));
		for(int i = 0; i < 9; i++) inv.set(i, ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE).build());
		inv.set(3, ItemBuilder.getSkullItem(cible));
		if(reports.size() > 45) {
			// there is pages
			
		} else {
			for(int i = 9; i < reports.size(); i++) {
				Report r = reports.get(i - 9);
				inv.set(i, ItemBuilder.Builder(Materials.APPLE).displayName(Adapter.getAdapter().getOfflinePlayer(r.getReportedBy()).getName())
						.lore(r.getReason()).build());
			}
		}
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, NegativityHolder nh) {
		
	}
}
