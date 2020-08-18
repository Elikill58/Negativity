package com.elikill58.negativity.common.special;

import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.Special;
import com.elikill58.negativity.universal.SpecialKeys;

public class McLeaks extends Special implements Listeners {
	
	public McLeaks() {
		super(SpecialKeys.MC_LEAKS, false, true);
	}
	
	@EventListener
	public void onLogin(LoginEvent e) {
		if(!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		NegativityAccount acc = NegativityAccount.get(e.getUUID());
		if(acc.isMcLeaks()) {
			if(getConfig().getBoolean("kick", false)) {
				e.setKickMessage(Messages.getMessage(NegativityAccount.get(e.getUUID()), "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
				e.setLoginResult(Result.KICK_BANNED);
			}
		}
	}
}
