package com.elikill58.negativity.common.inventories.holders.players;

import java.util.HashMap;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.universal.warn.Warn;

public class SeeWarnHolder extends NegativityHolder {
	
	private final OfflinePlayer cible;
	private final int page;
	private final HashMap<Integer, Warn> warnPerSlot = new HashMap<>();
	
	public SeeWarnHolder(OfflinePlayer cible, int page) {
		this.cible = cible;
		this.page = page;
	}

	public OfflinePlayer getCible() {
		return cible;
	}
	
	public int getPage() {
		return page;
	}
	
	public void add(int i, Warn w) {
		warnPerSlot.put(i, w);
	}
	
	public Warn get(int slot) {
		return warnPerSlot.get(slot);
	}
}
