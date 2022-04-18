package com.elikill58.negativity.common.inventories.players;

import java.util.List;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.common.inventories.holders.players.BanHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Sanction;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanInventory extends AbstractInventory<BanHolder> {
	
	public BanInventory() {
		super(NegativityInventory.BAN, BanHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		List<Sanction> sanctions = BanManager.getSanctions();
		Inventory inv = Inventory.createInventory("Ban", UniversalUtils.getMultipleOf(Sanction.getMaxSlot(sanctions) + 1, 9, 1, 54), new BanHolder(cible));
		sanctions.stream().filter((b) -> b.hasPermission(p)).forEach((ban) -> {
			inv.set(ban.getSlot(), ban.getItem(cible));
		});
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, BanHolder nh) {
		BanManager.getSanctions().stream().forEach((ban) -> {
			if(ban.hasPermission(p) && ban.getSlot() == e.getSlot()) {
				p.closeInventory();
				OfflinePlayer cible = nh.getCible();
				Adapter.getAdapter().runConsoleCommand(ban.getCommand().replaceAll("%name%", cible.getName()));
				p.sendMessage(Utils.coloredMessage(ban.getMessage().replaceAll("%name%", cible.getName()).replaceAll("%reason%", ban.getName())));
			}
		});
	}
}
