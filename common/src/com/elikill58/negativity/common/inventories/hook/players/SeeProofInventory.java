package com.elikill58.negativity.common.inventories.hook.players;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.players.SeeProofHolder;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Proof;
import com.elikill58.negativity.universal.storage.proof.NegativityProofStorage;
import com.elikill58.negativity.universal.utils.ChatUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SeeProofInventory extends AbstractInventory<SeeProofHolder> {

	public SeeProofInventory() {
		super(NegativityInventory.SEE_PROOF, SeeProofHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		OfflinePlayer cible = (OfflinePlayer) args[0];
		NegativityProofStorage.getStorage().getProof(cible.getUniqueId()).thenAcceptAsync(proofs -> {
			try {
				int page = (args.length == 1 ? 0 : (int) args[1]);
				if (page < 0)
					page = 0;
				Inventory inv = Inventory.createInventory("Proofs", UniversalUtils.getMultipleOf(proofs.size() + 9, 9, 1, 54), new SeeProofHolder(cible, page));
				for (int i = 1; i < 7; i++)
					inv.set(i, ItemBuilder.Builder(Materials.GRAY_STAINED_GLASS_PANE).build());
				inv.set(0, ItemBuilder.getSkullItem(cible, p));
				inv.set(7, Inventory.getBackItem(p));
				inv.set(8, Inventory.getCloseItem(p));

				int limit = 45;
				int offset = limit * page;
				int max = proofs.size() > (offset + limit) ? (offset + limit) : proofs.size() + offset;
				int slot = 9;
				for (int i = offset; i < max; i++) {
					if (proofs.size() <= i)
						continue;
					Proof r = proofs.get(i);
					Object[] placeholders = new Object[] { "%date%", ChatUtils.formatTime(r.getTime().getTime()), "%amount%", r.getAmount(), "%ping%", r.getPing(), "%report_type%",
							r.getReportType().getName(), "%check_name%", r.getCheckName(), "%reliability%", r.getReliability(), "%cheat%", r.getCheatKey().getName(), "%player_version%",
							r.getVersion().getName() };
					inv.set(slot++, ItemBuilder.Builder(Materials.BOOK).displayName(Messages.getMessage(p, "inventory.proof.item.name", placeholders))
							.lore(Messages.getMessage(p, "inventory.proof.item.lore", placeholders)).build());
				}
				if (page > 0)
					inv.set(3, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.page", "%id%", page)).build());
				if (proofs.size() > max)
					inv.set(5, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.page", "%id%", page + 2)).build());
				p.openInventory(inv);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().printError("Failed to open proof inventory for " + p.getName(), e);
			}
		});
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, SeeProofHolder nh) {
		if (m.equals(Materials.ARROW)) {
			int slot = e.getSlot();
			if (slot == 7) {
				if (nh.getCible() instanceof Player)
					InventoryManager.open(NegativityInventory.GLOBAL_PLAYER, p, nh.getCible());
				else
					InventoryManager.open(NegativityInventory.GLOBAL_PLAYER_OFFLINE, p, nh.getCible());
			} else if (e.getSlot() == 3)
				openInventory(p, nh.getCible(), nh.getPage() - 1);
			else
				openInventory(p, nh.getCible(), nh.getPage() + 1);
		}
	}
}
