package com.elikill58.negativity.api.impl.server.item;

import java.util.Arrays;
import java.util.Locale;

import com.elikill58.negativity.api.item.Material;

public class CompensatedMaterial extends Material {

	private final String id;
	
	public CompensatedMaterial(String id) {
		this.id = id.toLowerCase(Locale.ROOT);
	}
	
	@Override
	public boolean isSolid() {
		for(String search : Arrays.asList("water", "lava", "fire", "sign", "stem", "button"))
			if(id.contains(search))
				return false;
		return true;
	}

	@Override
	public boolean isTransparent() {
		for(String search : Arrays.asList("glass", "flower", "trapdoor", "torch", "sign", "stem", "button"))
			if(id.contains(search))
				return true;
		return false;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Object getDefault() {
		return this;
	}
	
	@Override
	public String toString() {
		return "CompensatedMaterial{id=" + id + "}";
	}
}
