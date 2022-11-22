package com.elikill58.negativity.integration.custombanplus;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

public class CustomBanPlusProcessorProvider implements BanProcessorProvider, PluginDependentExtension {

	@Override
	public String getId() {
		return "custombanplus";
	}

	@Override
	public BanProcessor create(Adapter adapter) {
		try {
			Class<?> processorClass = getClass().getClassLoader()
					.loadClass("com.elikill58.negativity.integration.custombanplus.CustomBanPlusProcessor");
			return (BanProcessor) processorClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPluginId() {
		return "CustomBansPlus";
	}
}
