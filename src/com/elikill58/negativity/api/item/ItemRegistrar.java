package com.elikill58.negativity.api.item;

import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class ItemRegistrar {
	
	public abstract Material get(String id, String... alias);

	public static ItemRegistrar getInstance() {
		return Adapter.getAdapter().getItemRegistrar();
	}
}
