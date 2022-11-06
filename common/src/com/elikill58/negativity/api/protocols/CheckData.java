package com.elikill58.negativity.api.protocols;

import com.elikill58.negativity.api.NegativityPlayer;

public abstract class CheckData {

	protected NegativityPlayer np;
	
	public CheckData(NegativityPlayer np) {
		this.np = np;
	}
	
	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
}
