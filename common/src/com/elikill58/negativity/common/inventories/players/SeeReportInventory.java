package com.elikill58.negativity.common.inventories.players;

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
import com.elikill58.negativity.common.inventories.holders.players.SeeReportHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.report.Report;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SeeReportInventory extends AbstractInventory<SeeReportHolder> {
	
	public SeeReportInventory() {
		super(NegativityInventory.SEE_REPORT, SeeReportHolder.class);
	}
	
	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		NegativityAccount na = NegativityAccount.get(cible.getUniqueId());
		List<Report> reports = na.getReports();
		int page = (args.length == 1 ? 0 : (int) args[1]);
		if(page < 0)
			page = 0;
		Inventory inv = Inventory.createInventory("Reports", UniversalUtils.getMultipleOf(reports.size() + 9, 9, 1, 54), new SeeReportHolder(cible, page));
		for(int i = 0; i < 9; i++) inv.set(i, ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE).build());
		inv.set(0, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(4, ItemBuilder.getSkullItem(cible));
		inv.set(8, Inventory.getCloseItem(p));
		
		int limit = 45;
		int offset = limit * page;
		int max = reports.size() > (offset + limit) ? (offset + limit) : reports.size() + offset;
		int slot = 9;
		for(int i = offset; i < max; i++) {
			if(reports.size() <= i)
				continue;
			Report r = reports.get(i);
			inv.set(slot++, ItemBuilder.Builder(Materials.APPLE).displayName(ChatColor.YELLOW + Adapter.getAdapter().getOfflinePlayer(r.getReportedBy()).getName())
					.lore(ChatColor.GRAY + r.getReason()).build());
		}
		if(page > 0)
			inv.set(3, ItemBuilder.Builder(Materials.ARROW).displayName("Page " + (page)).build());
		if(reports.size() > max)
			inv.set(5, ItemBuilder.Builder(Materials.ARROW).displayName("Page " + (page + 2)).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, SeeReportHolder nh) {
		SeeReportHolder rh = nh;
		if(m.equals(Materials.ARROW)) {
			int slot = e.getSlot();
			if(slot == 0) {
				if(rh.getCible() instanceof Player)
					InventoryManager.open(NegativityInventory.CHECK_MENU, p, rh.getCible());
				else
					InventoryManager.open(NegativityInventory.CHECK_MENU_OFFLINE, p, rh.getCible());
			} else if(e.getSlot() == 3)
				openInventory(p, rh.getCible(), rh.getPage() - 1);
			else
				openInventory(p, rh.getCible(), rh.getPage() + 1);
		}
	}
}
