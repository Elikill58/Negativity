package com.elikill58.negativity.common.inventories.hook.admin.warn;

import java.util.HashMap;
import java.util.Map.Entry;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.inventory.AbstractInventory;
import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.utils.InventoryUtils;
import com.elikill58.negativity.common.inventories.holders.admin.warn.WarnProcessorManagerHolder;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.warn.WarnManager;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;

public class WarnProcessorManagerInventory extends AbstractInventory<WarnProcessorManagerHolder> {

	public WarnProcessorManagerInventory() {
		super(NegativityInventory.WARN_PROCESSOR_MANAGER, WarnProcessorManagerHolder.class);
	}

	@Override
	public void openInventory(Player p, Object... obj) {
		WarnProcessorManagerHolder holder = new WarnProcessorManagerHolder();
		Inventory inv = Inventory.createInventory(Inventory.BAN_MANAGER_MENU, UniversalUtils.getMultipleOf(WarnManager.getProcessors().size() + 3, 9, 1, 54), holder);
		InventoryUtils.fillInventory(inv, Inventory.EMPTY);
		int slot = 0;
		for(Entry<String, WarnProcessor> entries : new HashMap<>(WarnManager.getProcessors()).entrySet()) {
			WarnProcessor proc = entries.getValue();
			holder.addItem(slot, entries.getKey());
			inv.set(slot++, ItemBuilder.Builder(Materials.PAPER).displayName(proc.getName()).lore(proc.getDescription()).build());
		}

		inv.set(inv.getSize() - 3, ItemBuilder.Builder(Materials.PAPER).displayName(Messages.getMessage(p, "inventory.warns.select", "%name%", WarnManager.getProcessorName())).lore(WarnManager.getProcessorDescription()).build());
		inv.set(inv.getSize() - 2, ItemBuilder.Builder(Materials.ARROW).displayName(Messages.getMessage(p, "inventory.back")).build());
		inv.set(inv.getSize() - 1, Inventory.getCloseItem(p));
		p.openInventory(inv);
	}

	@Override
	public void manageInventory(InventoryClickEvent e, Material m, Player p, WarnProcessorManagerHolder nh) {
		if(m.equals(Materials.ARROW)) {
			InventoryManager.open(NegativityInventory.WARN_MANAGER, p);
		} else {
			String str = nh.getBySlot(e.getSlot());
			if(str != null) {
				WarnManager.setProcessorId(str);
				openInventory(p);
			}
		}
	}
}
