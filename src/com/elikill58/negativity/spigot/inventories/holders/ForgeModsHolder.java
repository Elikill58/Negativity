package com.elikill58.negativity.spigot.inventories.holders;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class ForgeModsHolder extends NegativityHolder implements InventoryHolder {

	private OfflinePlayer cible;
	
	public ForgeModsHolder(OfflinePlayer cible) {
		this.cible = cible;
	}
	
	public OfflinePlayer getCible() {
		return cible;
	}
    
    @Override
    public Inventory getInventory() {
        return null;
    }
}
