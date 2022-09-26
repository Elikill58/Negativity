package com.elikill58.negativity.fabric.bridge;

import com.elikill58.negativity.api.inventory.NegativityHolder;

public interface NegativityHolderOwner {
	
	NegativityHolder negativity$getHolder();
	
	void negativity$setHolder(NegativityHolder holder);
}
