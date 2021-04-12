package com.elikill58.negativity.sponge.inventories.holders;

import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;

public class NegativityHolder implements Carrier {
	
	@Override
	public CarriedInventory<? extends Carrier> getInventory() {
		return null;
	}
}
