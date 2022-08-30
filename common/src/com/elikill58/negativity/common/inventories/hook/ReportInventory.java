package com.elikill58.negativity.common.inventories.hook;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.common.commands.ReportCommand;
import com.elikill58.negativity.common.inventories.holders.ReportHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Sanction;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ReportInventory extends AbstractInventory<ReportHolder> {
	
	private final List<Sanction> sanctions = new ArrayList<>();
	
	public ReportInventory() {
		super(NegativityInventory.REPORT, ReportHolder.class);
	}
	
	@Override
	public void load() {
		Configuration sanctionConfig = Adapter.getAdapter().getConfig().getSection("report");
		sanctionConfig.getKeys().forEach((key) -> sanctions.add(new Sanction(key, sanctionConfig.getSection(key))));
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory("Report " + cible.getName(), UniversalUtils.getMultipleOf(Sanction.getMaxSlot(sanctions), 9, 1, 54), new ReportHolder(cible));
		sanctions.stream().filter((b) -> b.hasPermission(p)).forEach((r) -> {
			inv.set(r.getSlot(), r.getItem(cible));
		});
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, ReportHolder nh) {
		sanctions.stream().filter((ban) -> ban.hasPermission(p) && ban.getSlot() == e.getSlot()).forEach((report) -> {
			p.closeInventory();
			Player cible = nh.getCible();
			ReportCommand.report(p, cible, report.getName());
			if(!report.getMessage().isEmpty())
				p.sendMessage(ChatColor.color(report.getMessage().replaceAll("%name%", cible.getName()).replaceAll("%reason%", report.getName())));
		});
	}
}
