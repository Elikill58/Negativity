package com.elikill58.negativity.minestom.impl.item;

import com.elikill58.negativity.api.item.Material;

public class MinestomMaterial extends Material {

	private final net.minestom.server.item.Material m;
	
	public MinestomMaterial(net.minestom.server.item.Material m) {
		this.m = m;
	}
	
	@Override
	public boolean isSolid() {
		return m.isBlock();
	}

	@Override
	public String getId() {
		return m.name();
	}

	@Override
	public boolean isTransparent() {
		return false;
	}

	@Override
	public Object getDefault() {
		return m;
	}
}
