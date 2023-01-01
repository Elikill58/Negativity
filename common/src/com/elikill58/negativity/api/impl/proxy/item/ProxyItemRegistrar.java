package com.elikill58.negativity.api.impl.proxy.item;

import com.elikill58.negativity.api.impl.server.item.CompensatedMaterial;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;

public class ProxyItemRegistrar extends ItemRegistrar {

	private static ProxyItemRegistrar instance = new ProxyItemRegistrar();
	public static ProxyItemRegistrar getInstance() {
		return instance;
	}
	
	@Override
	public Material get(String id, String... alias) {
		return new CompensatedMaterial(id);
	}
}
