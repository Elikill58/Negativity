package com.elikill58.negativity.common.inventory;

import java.util.Optional;

import com.elikill58.negativity.common.entity.Player;
import com.elikill58.negativity.common.inventory.AbstractInventory.NegativityInventory;

public class InventoryManager {

	public static Optional<AbstractInventory> getInventory(NegativityInventory type) {
		for(AbstractInventory inv : AbstractInventory.INVENTORIES)
			if(inv.getType().equals(type))
				return Optional.of(inv);
		return Optional.empty();
	}
	
	public static void open(NegativityInventory type, Player p, Object... args) {
		getInventory(type).ifPresent((inv) -> inv.openInventory(p, args));
	}
}
