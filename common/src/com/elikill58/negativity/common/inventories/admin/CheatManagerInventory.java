package com.elikill58.negativity.common.inventories.admin;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.Enchantment;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.common.inventories.holders.admin.CheatManagerHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CheatManagerInventory extends AbstractInventory<CheatManagerHolder> {

	public CheatManagerInventory() {
		super(NegativityInventory.ADMIN_CHEAT_MANAGER, CheatManagerHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Inventory inv = Inventory.createInventory(Inventory.CHEAT_MANAGER,
				UniversalUtils.getMultipleOf(Cheat.values().size() + 3, 9, 1, 54), new CheatManagerHolder());
		int slot = 0;
		for (Cheat c : Cheat.values())
			if (c.getMaterial() != null)
				inv.set(slot++, ItemBuilder.Builder(c.getMaterial()).displayName(c.getName()).lore(ChatColor.GRAY
						+ "State: " + Messages.getMessage(p, "inventory.manager." + (c.isActive() ? "enabled" : "disabled"))).enchantIf(Enchantment.UNBREAKING, 1, c.isActive()).build());
		inv.set(inv.getSize() - 2,
				ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}
	
	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheatManagerHolder nh) {
		if (m.equals(Materials.ARROW))
			InventoryManager.open(NegativityInventory.ADMIN, p);
		else {
			UniversalUtils.getCheatFromItem(m)
					.ifPresent((c) -> InventoryManager.open(NegativityInventory.ONE_CHEAT, p, c));
		}
	}
}
