package com.elikill58.negativity.sponge.inventories.holders;

public class CheatManagerHolder extends NegativityHolder {

	private boolean isFromAdmin = false;
	
	public CheatManagerHolder(boolean isFromAdmin) {
		this.isFromAdmin = isFromAdmin;
	}
	
	public boolean isFromAdmin() {
		return isFromAdmin;
	}
}
