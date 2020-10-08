package com.elikill58.negativity.common.inventories.negativity.players;

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
import com.elikill58.negativity.common.inventories.holders.negativity.players.KickHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Sanction;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class KickInventory extends AbstractInventory<KickHolder> {
	
	private final List<Sanction> sanctions = new ArrayList<>();
	
	public KickInventory() {
		super(NegativityInventory.KICK, KickHolder.class);
	}
	
	@Override
	public void load() {
		Configuration sanctionConfig = Adapter.getAdapter().getConfig().getSection("kicks");
		sanctionConfig.getKeys().forEach((key) -> sanctions.add(new Sanction(key, sanctionConfig.getSection(key))));
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Player cible = (Player) args[0];
		Inventory inv = Inventory.createInventory("Kick", UniversalUtils.getMultipleOf(Sanction.getMaxSlot(sanctions) + 2, 9, 1, 54), new KickHolder(cible));
		sanctions.stream().filter((b) -> b.hasPermission(p)).forEach((kick) -> {
			inv.set(kick.getSlot(), kick.getItem());
		});
		inv.set(inv.getSize() - 1, ItemBuilder.Builder(Materials.BARRIER).displayName(Messages.getMessage("inventory.close")).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, KickHolder nh) {
		sanctions.stream().filter((kick) -> kick.hasPermission(p) && kick.getSlot() == e.getSlot()).forEach((kick) -> {
			p.closeInventory();
			Player cible = nh.getCible();
			Adapter.getAdapter().runConsoleCommand(kick.getCommand().replaceAll("%name%", cible.getName()));
			p.sendMessage(Utils.coloredMessage(kick.getMessage().replaceAll("%name%", cible.getName()).replaceAll("%reason%", kick.getName())));
		});
	}
}
