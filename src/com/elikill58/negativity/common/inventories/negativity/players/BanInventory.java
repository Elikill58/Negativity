package com.elikill58.negativity.common.inventories.negativity.players;

import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.common.inventories.holders.negativity.players.BanHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanSanction;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanInventory extends AbstractInventory<BanHolder> {
	
	public BanInventory() {
		super(NegativityInventory.BAN, BanHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		List<BanSanction> sanctions = BanManager.getSanctions();
		Inventory inv = Inventory.createInventory("Ban", UniversalUtils.getMultipleOf(sanctions.size() + 2, 9, 1, 54), new BanHolder(cible));
		sanctions.stream().filter((b) -> b.hasPermission(p)).forEach((ban) -> {
			inv.set(ban.getSlot(), ItemBuilder.Builder(ban.getType()).displayName(ban.getName()).build());
		});
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage("inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, BanHolder nh) {
		BanManager.getSanctions().stream().forEach((ban) -> {
			if(ban.hasPermission(p) && ban.getSlot() == e.getSlot()) {
				p.closeInventory();
				Player cible = nh.getCible();
				Adapter.getAdapter().runConsoleCommand(ban.getCommand().replaceAll("%name%", cible.getName()));
				p.sendMessage(Utils.coloredMessage(ban.getMessage().replaceAll("%name%", cible.getName()).replaceAll("%reason%", ban.getName())));
			}
		});
	}
}
