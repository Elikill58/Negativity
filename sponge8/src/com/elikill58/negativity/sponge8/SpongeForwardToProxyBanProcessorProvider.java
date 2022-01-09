package com.elikill58.negativity.sponge8;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.PlatformDependentExtension;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;

public class SpongeForwardToProxyBanProcessorProvider implements BanProcessorProvider, PlatformDependentExtension {
	
	@Override
	public String getId() {
		return ForwardToProxyBanProcessor.PROCESSOR_ID;
	}
	
	@Nullable
	@Override
	public BanProcessor create(Adapter adapter) {
		return new ForwardToProxyBanProcessor(SpongeNegativity::sendPluginMessage);
	}
	
	@Override
	public Platform getPlatform() {
		return Platform.SPONGE8;
	}
}
