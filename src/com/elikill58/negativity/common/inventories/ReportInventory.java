package com.elikill58.negativity.common.inventories;

import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.ReportHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Report;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ReportInventory extends AbstractInventory<ReportHolder> {
	
	public ReportInventory() {
		super(NegativityInventory.REPORT, ReportHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		NegativityAccount na = NegativityAccount.get(cible.getUniqueId());
		List<Report> reports = na.getReports();
		int offset = (args.length == 1 ? 0 : (int) args[1]);
		if(offset < 0)
			offset = 0;
		Inventory inv = Inventory.createInventory("Reports", UniversalUtils.getMultipleOf(reports.size() + 9, 9, 1, 54), new ReportHolder(cible, offset));
		for(int i = 0; i < 9; i++) inv.set(i, ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE).build());
		inv.set(0, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(4, ItemBuilder.getSkullItem(cible));
		inv.set(8, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage(p, "inventory.close")).build());
		int max = reports.size() > 45 ? 45 : reports.size() + offset;
		int slot = 9;
		int i;
		for(i = offset; i < max; i++) {
			Report r = reports.get(i);
			inv.set(slot++, ItemBuilder.Builder(Materials.APPLE).displayName(Adapter.getAdapter().getOfflinePlayer(r.getReportedBy()).getName())
					.lore(r.getReason()).build());
		}
		if(offset > 0)
			inv.set(2, ItemBuilder.Builder(Materials.ARROW).displayName("Last").build());
		if(slot == 45)
			inv.set(5, ItemBuilder.Builder(Materials.ARROW).displayName("Next").build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, ReportHolder nh) {
		ReportHolder rh = (ReportHolder) nh;
		if(m.equals(Materials.ARROW)) {
			int slot = e.getSlot();
			if(slot == 0)
				InventoryManager.open(NegativityInventory.CHEAT_MANAGER, p, rh.getCible());
			else if(e.getSlot() == 2)
				openInventory(p, rh.getCible(), rh.getOffset() - 45);
			else
				openInventory(p, rh.getCible(), rh.getOffset() + 45);
		}
	}
}
