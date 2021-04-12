package com.elikill58.negativity.spigot.inventories.holders;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CheatManagerHolder extends NegativityHolder implements InventoryHolder {

	private boolean isFromAdmin = false;
	
	public CheatManagerHolder(boolean isFromAdmin) {
		this.isFromAdmin = isFromAdmin;
	}
	
	public boolean isFromAdmin() {
		return isFromAdmin;
	}
    
    @Override
    public Inventory getInventory() {
        return null;
    }
}
