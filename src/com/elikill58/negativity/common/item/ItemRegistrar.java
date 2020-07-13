package com.elikill58.negativity.common.item;

import com.elikill58.negativity.universal.adapter.Adapter;

public abstract class ItemRegistrar {
	
	public abstract Material get(String id);

	public static ItemRegistrar getInstance() {
		return Adapter.getAdapter().getItemRegistrar();
	}
}
