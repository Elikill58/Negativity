package com.elikill58.negativity.minestom.impl.inventory;

import java.util.HashMap;

import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.minestom.impl.inventory.holders.MinestomNegativityHolder;

public class HolderById {

	private static final HashMap<Integer, NegativityHolder> holders = new HashMap<>();
	public static NegativityHolder getHolder(int windowId) {
		return holders.get(windowId);
	}
	public static NegativityHolder getOrCreate(int windowId) {
		return holders.getOrDefault(windowId, new MinestomNegativityHolder());
	}
	
	public static void add(int windowId, NegativityHolder holder) {
		holders.put(windowId, holder);
	}
}
