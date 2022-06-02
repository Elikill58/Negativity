package com.elikill58.negativity.universal.ban.processor;

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
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyRevokeBanMessage;

public class ForwardToProxyBanProcessor implements BanProcessor {

	public static final String PROCESSOR_ID = "proxy";

	@Override
	public BanResult executeBan(Ban ban) {
		// get(0) because there is at least the cheating player
		Adapter.getAdapter().getOnlinePlayers().get(0).sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new ProxyExecuteBanMessage(ban));
		return new BanResult(BanResultType.DONE, ban);
	}
	
	@Override
	public BanResult revokeBan(UUID playerId) {
		Adapter.getAdapter().getOnlinePlayers().get(0).sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new ProxyRevokeBanMessage(playerId));
		return new BanResult(BanResultType.DONE, new Ban(playerId, "", "", BanType.UNKNOW, -1, null, null, BanStatus.REVOKED, -1, System.currentTimeMillis()));
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		// If this processor is active, companion plugins on the proxies are supposed to handle bans
		return null;
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		return Collections.emptyList();
	}
	
	@Override
	public List<Ban> getAllBans() {
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

	public static class Provider implements BanProcessorProvider, PlatformDependentExtension {
		@Override
		public String getId() {
			return PROCESSOR_ID;
		}
	
		@Override
		public @Nullable BanProcessor create(Adapter adapter) {
			return new ForwardToProxyBanProcessor();
		}

		@Override
		public List<Platform> getPlatforms() {
			return Arrays.asList(Platform.values()).stream().filter(p -> !p.isProxy()).collect(Collectors.toList());
		}
	}
}
