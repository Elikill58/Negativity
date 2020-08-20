package com.elikill58.negativity.universal.bypass;

import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.adapter.Adapter;

public class BypassManager {

	public static void loadBypass() {
		Adapter ada = Adapter.getAdapter();
		Configuration configItems = ada.getConfig().getSection("items");
		if(configItems != null) {
			configItems.getKeys().forEach((key) -> {
				new ItemUseBypass(key, configItems.getString(key + ".cheats"), configItems.getString(key + ".when"));
			});
		}
	}
}
