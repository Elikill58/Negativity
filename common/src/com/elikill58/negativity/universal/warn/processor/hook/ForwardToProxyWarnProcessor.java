package com.elikill58.negativity.universal.warn.processor.hook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Platform;
import com.elikill58.negativity.universal.PlatformDependentExtension;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteWarnMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyRevokeBanMessage;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.WarnResult.WarnResultType;
import com.elikill58.negativity.universal.warn.processor.WarnProcessor;
import com.elikill58.negativity.universal.warn.processor.WarnProcessorProvider;

public class ForwardToProxyWarnProcessor implements WarnProcessor {

	public static final String PROCESSOR_ID = "proxy";

	@Override
	public WarnResult executeWarn(Warn ban) {
		// get(0) because there is at least the cheating player
		Adapter.getAdapter().getOnlinePlayers().get(0).sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new ProxyExecuteWarnMessage(ban));
		return new WarnResult(WarnResultType.DONE, ban);
	}
	
	@Override
	public WarnResult revokeWarn(UUID playerId) {
		Adapter.getAdapter().getOnlinePlayers().get(0).sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new ProxyRevokeBanMessage(playerId));
		return new WarnResult(WarnResultType.DONE, new Warn(playerId, "", "", SanctionnerType.UNKNOW, null, -1));
	}

	@Override
	public WarnResult revokeWarn(Warn warn) {
		Adapter.getAdapter().getOnlinePlayers().get(0).sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new ProxyRevokeBanMessage(warn.getPlayerId()));
		return new WarnResult(WarnResultType.DONE, warn);
	}

	@Override
	public List<Warn> getActiveWarn(UUID playerId) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Warn> getActiveWarnOnSameIP(String ip) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Warn> getAllWarns() {
		return Collections.emptyList();
	}
	
	@Override
	public String getName() {
		return "Forward To Proxy";
	}
	
	@Override
	public List<String> getDescription() {
		return Arrays.asList(ChatColor.YELLOW + "Send request to proxy.", "&eDepend of proxy config.");
	}

	public static class Provider implements WarnProcessorProvider, PlatformDependentExtension {
		@Override
		public String getId() {
			return PROCESSOR_ID;
		}
	
		@Override
		public @Nullable WarnProcessor create(Adapter adapter) {
			return new ForwardToProxyWarnProcessor();
		}

		@Override
		public List<Platform> getPlatforms() {
			return Arrays.asList(Platform.values()).stream().filter(p -> !p.isProxy()).collect(Collectors.toList());
		}
	}
}
