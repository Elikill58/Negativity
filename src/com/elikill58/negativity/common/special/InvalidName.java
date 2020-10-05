package com.elikill58.negativity.common.special;

import java.util.UUID;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Special;
import com.elikill58.negativity.universal.SpecialKeys;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class InvalidName extends Special implements Listeners {

	public InvalidName() {
		super(SpecialKeys.INVALID_NAME, false, true);
	}

	@EventListener
	public void onLogin(LoginEvent e) {
		if (!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		UUID playerId = e.getUUID();
		NegativityAccount account = NegativityAccount.get(playerId);
		if (!UniversalUtils.isValidName(e.getName())) {
			// check for ban / kick only if the player is not already banned
			if (getConfig().getBoolean("ban.active")) {
				if (!BanManager.banActive) {
					Adapter ada = Adapter.getAdapter();
					ada.getLogger().warn("Cannot ban player " + e.getName() + " for " + getName() + " because ban is NOT config.");
					ada.getLogger().warn("Please, enable ban in config and restart your server");
					if (getConfig().getBoolean("kick")) {
						e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
						e.setLoginResult(Result.KICK_OTHER);
					}
				} else {
					BanManager.executeBan(Ban.active(playerId, getName(), "Negativity", BanType.PLUGIN,
							getConfig().getInt("ban.time"), getName(), e.getAddress().getHostAddress()));
					e.setLoginResult(Result.KICK_BANNED);
				}
			} else if (getConfig().getBoolean("kick")) {
				e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
				e.setLoginResult(Result.KICK_OTHER);
			}
		}
	}
}
