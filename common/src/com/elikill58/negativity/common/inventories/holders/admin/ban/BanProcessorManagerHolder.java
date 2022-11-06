package com.elikill58.negativity.common.inventories.holders.admin.ban;

import java.util.HashMap;

import com.elikill58.negativity.api.inventory.NegativityHolder;

public class BanProcessorManagerHolder extends NegativityHolder {

	private final HashMap<Integer, String> processors = new HashMap<>();
	
	public void addItem(int slot, String proc) {
		processors.put(slot, proc);
	}
	
	public String getBySlot(int slot) {
		return processors.get(slot);
	}
	
	@Override
	public NegativityHolder getBasicHolder() {
		return this;
	}
}
