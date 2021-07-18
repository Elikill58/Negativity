package com.elikill58.negativity.spigot.inventories.holders;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CheckMenuHolder extends NegativityHolder implements InventoryHolder {

	private Player cible;
	
	public CheckMenuHolder(Player cible) {
		this.cible = cible;
	}
	
	public Player getCible() {
		return cible;
	}
    
    @Override
    public Inventory getInventory() {
        return null;
    }
}
