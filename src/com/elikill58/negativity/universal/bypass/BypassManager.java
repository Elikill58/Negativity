package com.elikill58.negativity.universal.bypass;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

public class BypassManager {

	public static void loadBypass() {
		Adapter ada = Adapter.getAdapter();
		ConfigAdapter configItems = ada.getConfig().getChild("items");
		if(configItems != null) {
			configItems.getKeys().forEach((key) -> {
				new ItemUseBypass(key, configItems.getString(key + ".cheats"), configItems.getString(key + ".when"));
			});
		}
	}
}
