package com.elikill58.negativity.spigot.inventories.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.elikill58.negativity.universal.Cheat;

public class OneCheatHolder extends NegativityHolder implements InventoryHolder {

	private final Cheat c;
	
	public OneCheatHolder(Cheat c) {
		this.c = c;
	}
	
	public Cheat getCheat() {
		return c;
	}
    
    @Override
    public Inventory getInventory() {
        return null;
    }
}
