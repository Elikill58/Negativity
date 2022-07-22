package com.elikill58.negativity.common.inventories.hook.admin;

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
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.common.inventories.holders.admin.CheatChecksHolder;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class CheatChecksInventory extends AbstractInventory<CheatChecksHolder> {

	public CheatChecksInventory() {
		super(NegativityInventory.CHEAT_CHECKS, CheatChecksHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... args) {
		Cheat c = (Cheat) args[0];
		CheatChecksHolder holder = new CheatChecksHolder(c);
		Inventory inv = Inventory.createInventory(c.getName(), UniversalUtils.getMultipleOf(c.getChecks().size() + 3, 9, 1), holder);
		
		int slot = 0;
		for(Check check : c.getChecks()) {
			holder.add(slot, check);
			String name = String.valueOf(check.name().charAt(0)).toUpperCase() + check.name().substring(1);
			inv.set(slot++, ItemBuilder.Builder(c.getMaterial()).displayName(name).lore(ChatColor.GRAY + check.description()).enchantIf(Enchantment.UNBREAKING, 1, c.checkActive(check)).build());
		}
		
		inv.set(inv.getSize() - 2, Inventory.getBackItem(p));
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, CheatChecksHolder nh) {
		Cheat c = nh.getCheat();
		if (m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.ONE_CHEAT, p, c);
			return;
		}
		Check check = nh.get(e.getSlot());
		if(check != null) {
			c.setCheckActive(check, !c.checkActive(check));
			c.saveConfig();
			openInventory(p, c);
		}
	}
}
