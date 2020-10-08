package com.elikill58.negativity.common.inventories;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.common.inventories.holders.ReportHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.report.ReportSanction;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ReportInventory extends AbstractInventory<ReportHolder> {
	
	private final List<ReportSanction> sanctions = new ArrayList<>();
	
	public ReportInventory() {
		super(NegativityInventory.REPORT, ReportHolder.class);
	}
	
	@Override
	public void load() {
		Configuration sanctionConfig = Adapter.getAdapter().getConfig().getSection("report");
		sanctionConfig.getKeys().forEach((key) -> sanctions.add(new ReportSanction(key, sanctionConfig.getSection(key))));
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory("Report " + cible.getName(), UniversalUtils.getMultipleOf(sanctions.size() + 2, 9, 1, 54), new ReportHolder(cible));
		sanctions.stream().filter((b) -> b.hasPermission(p)).forEach((r) -> {
			inv.set(r.getSlot(), ItemBuilder.Builder(r.getType()).displayName(r.getName()).build());
		});
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage("inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, ReportHolder nh) {
		sanctions.stream().filter((ban) -> ban.hasPermission(p) && ban.getSlot() == e.getSlot()).forEach((ban) -> {
			p.closeInventory();
			Player cible = nh.getCible();
			Adapter.getAdapter().runConsoleCommand(ban.getCommand().replaceAll("%name%", cible.getName()));
			p.sendMessage(Utils.coloredMessage(ban.getMessage().replaceAll("%name%", cible.getName()).replaceAll("%reason%", ban.getName())));
		});
	}
}
