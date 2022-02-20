package com.elikill58.negativity.common.special;

import java.util.UUID;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.EventPriority;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;

public class BannedName extends Special implements Listeners {

	public BannedName() {
		super(SpecialKeys.BANNED_NAME, Materials.ANVIL, false);
	}

	@EventListener(priority = EventPriority.POST)
	public void onLogin(LoginEvent e) {
		if (!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		UUID playerId = e.getUUID();
		NegativityAccount account = NegativityAccount.get(playerId);
		if (isBannedName(e.getName()) || isBannedRegex(e.getName())) {
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

	public boolean isBannedRegex(String name) {
		return getConfig().getStringList("banned_names").stream().filter(name::matches).count() > 0;
	}

	public boolean isBannedName(String name) {
		name = name.toLowerCase();
		return getConfig().getStringList("banned_names").stream().map(String::toLowerCase).filter(name::contains).count() > 0;
	}
}
