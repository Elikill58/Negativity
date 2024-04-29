package com.elikill58.negativity.common.integration.geysermc.geysermc;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.bedrock.data.BedrockClientDataGetter;
import com.elikill58.negativity.universal.bedrock.data.BedrockClientDataProvider;

public class GeyserClientDataProvider implements BedrockClientDataProvider, PluginDependentExtension {

	@Override
	public BedrockClientDataGetter create(Adapter adapter) {
		try {
			Class<?> processorClass = getClass().getClassLoader()
					.loadClass("com.elikill58.negativity.common.integration.geysermc.geysermc.GeyserClientDataGetter");
			return (BedrockClientDataGetter) processorClass.getConstructor().newInstance();
		} catch (UnsupportedClassVersionError e) {
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getPluginId() {
		return "Geyser-" + Adapter.getAdapter().getPlatformID().getCompleteName();
	}
}
