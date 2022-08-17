package com.elikill58.negativity.common.inventories.hook.players;

import java.util.List;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.common.inventories.holders.players.WarnHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Sanction;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.warn.WarnManager;

public class WarnInventory extends AbstractInventory<WarnHolder> {
	
	public WarnInventory() {
		super(NegativityInventory.WARN, WarnHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		List<Sanction> sanctions = WarnManager.getSanctions();
		Inventory inv = Inventory.createInventory("Warn", UniversalUtils.getMultipleOf(Sanction.getMaxSlot(sanctions) + 1, 9, 1, 54), new WarnHolder(cible));
		sanctions.stream().filter((b) -> b.hasPermission(p)).forEach((ban) -> {
			inv.set(ban.getSlot(), ban.getItem(cible));
		});
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, WarnHolder nh) {
		WarnManager.getSanctions().stream().forEach((warn) -> {
			if(warn.hasPermission(p) && warn.getSlot() == e.getSlot()) {
				p.closeInventory();
				OfflinePlayer cible = nh.getCible();
				Adapter.getAdapter().runConsoleCommand(warn.getCommand().replace("%name%", cible.getName()));
				p.sendMessage(Utils.coloredMessage(warn.getMessage().replace("%name%", cible.getName()).replace("%reason%", warn.getName())));
			}
		});
	}
}
