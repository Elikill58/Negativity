package com.elikill58.negativity.common.inventories.hook.admin.detections;

import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemFlag;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.admin.detections.CheatDescriptionHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatCategory;
import com.elikill58.negativity.universal.detections.Cheat.CheatDescription;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CheatDescriptionInventory extends AbstractInventory<CheatDescriptionHolder> {

	public CheatDescriptionInventory() {
		super(NegativityInventory.CHEAT_DESCRIPTION, CheatDescriptionHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		CheatDescriptionHolder holder = new CheatDescriptionHolder();
		Inventory inv = Inventory.createInventory(Inventory.CHEAT_MANAGER,
				UniversalUtils.getMultipleOf(CheatDescription.values().length + 9, 9, 1, 54), holder);

		int slot = 0;
		for (CheatCategory cc : CheatCategory.values()) {
			holder.add(slot, cc);
			List<Cheat> cheats = getCheatWithCategory(cc);
			int enabledCheat = cheats.stream().filter(Cheat::isActive).collect(Collectors.toList()).size();
			inv.set(slot++, ItemBuilder.Builder(cc.getType()).displayName(cc.getName())
					.lore(ChatColor.GRAY + "Type: " + ChatColor.YELLOW + cc.name().toLowerCase(),
							ChatColor.GRAY + "Cheat enabled: " + ChatColor.YELLOW + enabledCheat + "/" + cheats.size())
					.itemFlag(ItemFlag.HIDE_ATTRIBUTES).enchantIf(enabledCheat > (cheats.size() / 2)).build());
		}
		slot = 9;
		for (CheatDescription cc : CheatDescription.values()) {
			holder.add(slot, cc);
			List<Cheat> cheats = getCheatWithDescription(cc);
			int enabledCheat = cheats.stream().filter(Cheat::isActive).collect(Collectors.toList()).size();
			inv.set(slot++, ItemBuilder.Builder(Materials.PAPER).displayName(cc.getName())
					.lore(ChatColor.GRAY + "Action: " + ChatColor.YELLOW + cc.getDescription(),
							ChatColor.GRAY + "Cheat enabled: " + ChatColor.YELLOW + enabledCheat + "/" + cheats.size())
					.enchantIf(enabledCheat > (cheats.size() / 2)).build());
		}

		inv.set(inv.getSize() - 2,
				ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	private List<Cheat> getCheatWithCategory(CheatCategory cc) {
		return Cheat.getCheatByKeys().values().stream().filter(c -> c.getCheatCategory().equals(cc))
				.collect(Collectors.toList());
	}

	private List<Cheat> getCheatWithDescription(CheatDescription cc) {
		return Cheat.getCheatByKeys().values().stream().filter(c -> c.hasOption(cc)).collect(Collectors.toList());
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheatDescriptionHolder nh) {
		if (m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.ADMIN_CHEAT_MANAGER, p);
		} else {
			List<Cheat> cheats = null;
			CheatCategory cat = nh.getCategory(e.getSlot());
			if (cat != null) {
				cheats = getCheatWithCategory(cat);
			} else {
				CheatDescription cc = nh.getDescription(e.getSlot());
				if (cc != null) {
					cheats = getCheatWithDescription(cc);
				}
			}
			if (cheats == null)
				return;
			int enabledCheat = cheats.stream().filter(Cheat::isActive).collect(Collectors.toList()).size();
			boolean newVal = enabledCheat <= (cheats.size() / 2);
			cheats.forEach(c -> c.setActive(newVal));
			openInventory(p);
		}
	}

}
