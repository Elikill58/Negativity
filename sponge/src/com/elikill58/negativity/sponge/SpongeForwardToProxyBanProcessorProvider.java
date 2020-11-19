package com.elikill58.negativity.sponge;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;

public class SpongeForwardToProxyBanProcessorProvider implements BanProcessorProvider {
	
	@Override
	public String getId() {
		return ForwardToProxyBanProcessor.PROCESSOR_ID;
	}
	
	@Nullable
	@Override
	public BanProcessor create(Adapter adapter) {
		return new ForwardToProxyBanProcessor(SpongeNegativity::sendPluginMessage);
	}
}
