package com.elikill58.negativity.common.inventories.holders.admin;

import java.util.HashMap;

import com.elikill58.negativity.api.inventory.NegativityHolder;

public class LangHolder extends NegativityHolder {
	
	private final HashMap<Integer, String> langBySlot = new HashMap<>();
	
	public void addLang(String lang, int slot) {
		langBySlot.put(slot, lang);
	}
	
	public HashMap<Integer, String> getLangBySlot() {
		return langBySlot;
	}
}
