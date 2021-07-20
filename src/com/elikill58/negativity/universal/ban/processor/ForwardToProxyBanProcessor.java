package com.elikill58.negativity.universal.ban.processor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyExecuteBanMessage;
import com.elikill58.negativity.universal.pluginMessages.ProxyRevokeBanMessage;

public class ForwardToProxyBanProcessor implements BanProcessor {

	public static final String PROCESSOR_ID = "proxy";

	private final Consumer<byte[]> pluginMessageSender;

	public ForwardToProxyBanProcessor(Consumer<byte[]> pluginMessageSender) {
		this.pluginMessageSender = pluginMessageSender;
	}

	@Nullable
	@Override
	public Ban executeBan(Ban ban) {
		try {
			byte[] rawMessage = NegativityMessagesManager.writeMessage(new ProxyExecuteBanMessage(ban));
			pluginMessageSender.accept(rawMessage);
			Adapter.getAdapter().debug("Sent ban to proxy " + ban.getPlayerId().toString());
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ProxyBanMessage: " + e.getMessage());
			e.printStackTrace();
		}
		return ban;
	}

	@Nullable
	@Override
	public Ban revokeBan(UUID playerId) {
		try {
			byte[] rawMessage = NegativityMessagesManager.writeMessage(new ProxyRevokeBanMessage(playerId));
			pluginMessageSender.accept(rawMessage);
		} catch (IOException e) {
			Adapter.getAdapter().getLogger().error("Could not write ProxyBanMessage: " + e.getMessage());
			e.printStackTrace();
		}
		return new Ban(playerId, "", "", BanType.UNKNOW, -1, null, BanStatus.REVOKED, -1, System.currentTimeMillis());
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
	public List<Ban> getAllBans() {
		return Collections.emptyList();
	}
}
