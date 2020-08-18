package com.elikill58.negativity.api.item;

import com.elikill58.negativity.api.NegativityObject;

public abstract class Material extends NegativityObject {
	
	public abstract boolean isSolid();
	
	public abstract boolean isTransparent();
	
	public abstract String getId();
	
}
