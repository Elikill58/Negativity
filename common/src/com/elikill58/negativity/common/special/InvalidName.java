package com.elikill58.negativity.common.special;

import java.util.UUID;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.bedrock.BedrockPlayerManager;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class InvalidName extends Special implements Listeners {

	public InvalidName() {
		super(SpecialKeys.INVALID_NAME, Materials.PAPER);
	}

	@EventListener
	public void onLogin(LoginEvent e) {
		if (!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		UUID playerId = e.getUUID();
		NegativityAccount account = NegativityAccount.get(playerId);
		if (UniversalUtils.isValidName(e.getName())) // valid name, ignoring
			return;
		if (BedrockPlayerManager.isBedrockPlayer(playerId) || couldBeAdded(e.getName())) // bedrock player, we are sure about this
			return;
		if (ProxyCompanionManager.isIntegrationEnabled() && UniversalUtils.isValidName(e.getName().substring(1)))
			return; // seems to be geyser player with invalid char at begin

		// check for ban / kick only if the player is not already banned
		if (getConfig().getBoolean("ban")) {
			if (!BanManager.banActive) {
				Adapter ada = Adapter.getAdapter();
				ada.getLogger().warn("Cannot ban player " + e.getName() + " for " + getName() + " because ban is NOT config.");
				ada.getLogger().warn("Please, enable ban in config and restart your server");
				if (getConfig().getBoolean("kick")) {
					e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
					e.setLoginResult(Result.KICK_OTHER);
				}
			} else {
				BanManager.executeBan(Ban.active(playerId, getName(), "Negativity", SanctionnerType.PLUGIN, getConfig().getInt("ban.time"), getName(), e.getAddress().getHostAddress()));
				e.setLoginResult(Result.KICK_BANNED);
			}
		} else if (getConfig().getBoolean("kick")) {
			e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
			e.setLoginResult(Result.KICK_OTHER);
		} else
			Adapter.getAdapter().getLogger().info("Player " + e.getName() + " has an invalid name.");
	}
	
	private boolean couldBeAdded(String name) {
		return name.replaceAll("[0-9A-Za-z-_*]{3," + name.length() + "}", "").replace("*", "").replace(".", "").length() > 0;
	}
}
