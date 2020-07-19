package com.elikill58.negativity.api.item;

public interface Material {
	
	public boolean isSolid();
	
	public boolean isTransparent();
	
	public String getId();
	
	public Object getDefaultMaterial();
	
}
