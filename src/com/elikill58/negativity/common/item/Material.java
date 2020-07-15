package com.elikill58.negativity.common.item;

public interface Material {
	
	public boolean isSolid();
	
	public boolean isTransparent();
	
	public String getId();
	
	public Object getDefaultMaterial();
}
