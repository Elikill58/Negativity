package com.elikill58.negativity.common.inventories.hook.players;

import java.util.Comparator;
import java.util.List;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.players.SeeWarnHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.utils.ChatUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnManager;

public class SeeWarnInventory extends AbstractInventory<SeeWarnHolder> {

	public SeeWarnInventory() {
		super(NegativityInventory.WARN_SEE, SeeWarnHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		List<Warn> warns = WarnManager.getWarn(cible.getUniqueId());
		warns.sort(Comparator.comparing(Warn::getId, Comparator.reverseOrder()));
		warns.sort(Comparator.comparing(Warn::isActive, Comparator.reverseOrder()));
		int page = (args.length == 1 ? 0 : (int) args[1]);
		if (page < 0)
			page = 0;
		SeeWarnHolder holder = new SeeWarnHolder(cible, page);
		Inventory inv = Inventory.createInventory("Reports", UniversalUtils.getMultipleOf(warns.size() + 9, 9, 1, 54), holder);
		for (int i = 0; i < 9; i++)
			inv.set(i, ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE).build());
		inv.set(0, ItemBuilder.getSkullItem(cible));
		inv.set(4,
				ItemBuilder.Builder(Materials.EMERALD)
						.displayName(Messages.getMessage(p, "inventory.warns.clear.name", "%name%", cible.getName()))
						.lore(Messages.getMessage(p, "inventory.warns.clear.lore", "%name%", cible.getName())).build());
		inv.set(7, Inventory.getBackItem(p));
		inv.set(8, Inventory.getCloseItem(p));

		int limit = 45;
		int offset = limit * page;
		int max = warns.size() > (offset + limit) ? (offset + limit) : warns.size() + offset;
		int slot = 9;
		for (int i = offset; i < max; i++) {
			if (warns.size() <= i)
				continue;
			Warn w = warns.get(i);
			Object[] warnPlaceholder = new Object[] { "%id%", w.getId(), "%reason%", w.getReason(), "%warned_by%",
					w.getWarnedByName(), "%execution%", ChatUtils.formatTime(w.getExecutionTime()), "%revoked_by%",
					w.getRevocationByName(), "%revoked_date%", ChatUtils.formatTime(w.getRevocationTime()) };
			holder.add(slot, w);
			if (w.isActive())
				inv.set(slot++, ItemBuilder.Builder(Materials.GOLDEN_APPLE)
						.displayName(Messages.getMessage(p, "inventory.warns.show.active.name", warnPlaceholder))
						.lore(Messages.getMessage(p, "inventory.warns.show.active.lore", warnPlaceholder)).build());
			else
				inv.set(slot++, ItemBuilder.Builder(Materials.APPLE)
						.displayName(Messages.getMessage(p, "inventory.warns.show.revoked.name", warnPlaceholder))
						.lore(Messages.getMessage(p, "inventory.warns.show.revoked.lore", warnPlaceholder)).build());
		}
		if (page > 0)
			inv.set(3, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.page", "%id%", page)).build());
		if (warns.size() > max)
			inv.set(5, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.page", "%id%", page + 2)).build());
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, SeeWarnHolder nh) {
		if (m.equals(Materials.EMERALD)) {
			WarnManager.revokeWarn(nh.getCible().getUniqueId(), p.getUniqueId().toString());
			openInventory(p, nh.getCible(), nh.getPage());
		} else if (m.equals(Materials.ARROW)) {
			if (e.getSlot() == 7) {
				if (nh.getCible() instanceof Player)
					InventoryManager.open(NegativityInventory.GLOBAL_PLAYER, p, nh.getCible());
				else
					InventoryManager.open(NegativityInventory.GLOBAL_PLAYER_OFFLINE, p, nh.getCible());
			} else if (e.getSlot() == 3)
				openInventory(p, nh.getCible(), nh.getPage() - 1);
			else
				openInventory(p, nh.getCible(), nh.getPage() + 1);
		} else {
			Warn w = nh.get(e.getSlot());
			if(w == null)
				return;
			if(w.isActive()) {
				WarnManager.revokeWarn(w, p.getUniqueId().toString());
				openInventory(p, nh.getCible(), nh.getPage());
			}
		}
	}
}
