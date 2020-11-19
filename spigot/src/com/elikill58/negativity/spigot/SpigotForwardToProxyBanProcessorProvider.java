package com.elikill58.negativity.spigot;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;

public class SpigotForwardToProxyBanProcessorProvider implements BanProcessorProvider {

	@Override
	public String getId() {
		return ForwardToProxyBanProcessor.PROCESSOR_ID;
	}

	@Override
	public BanProcessor create(Adapter adapter) {
		return new ForwardToProxyBanProcessor(SpigotNegativity::sendPluginMessage);
	}
}
