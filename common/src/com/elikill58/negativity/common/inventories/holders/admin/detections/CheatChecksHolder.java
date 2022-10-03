package com.elikill58.negativity.common.inventories.holders.admin.detections;

import java.util.HashMap;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.universal.detections.Cheat;

public class CheatChecksHolder extends NegativityHolder {

	private final Cheat cheat;
	private final HashMap<Integer, Check> checkPerSlot = new HashMap<>();
	
	public CheatChecksHolder(Cheat c) {
		this.cheat = c;
	}
	
	public Cheat getCheat() {
		return cheat;
	}
	
	public void add(int slot, Check c) {
		checkPerSlot.put(slot, c);
	}
	
	public Check get(int slot) {
		return checkPerSlot.get(slot);
	}
}
