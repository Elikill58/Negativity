package com.elikill58.negativity.common.inventories.holders.admin;

import java.util.HashMap;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.universal.detections.Cheat.CheatCategory;
import com.elikill58.negativity.universal.detections.Cheat.CheatDescription;

public class CheatDescriptionHolder extends NegativityHolder {

	private final HashMap<Integer, CheatCategory> categoryBySlot = new HashMap<>();
	private final HashMap<Integer, CheatDescription> descriptionBySlot = new HashMap<>();

	public void add(int slot, CheatCategory cc) {
		categoryBySlot.put(slot, cc);
	}
	
	public CheatCategory getCategory(int slot) {
		return categoryBySlot.get(slot);
	}

	public void add(int slot, CheatDescription cc) {
		descriptionBySlot.put(slot, cc);
	}
	
	public CheatDescription getDescription(int slot) {
		return descriptionBySlot.get(slot);
	}
}
