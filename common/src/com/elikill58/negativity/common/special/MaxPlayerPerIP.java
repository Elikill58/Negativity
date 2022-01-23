package com.elikill58.negativity.common.special;

import java.util.stream.Collectors;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Special;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.keys.SpecialKeys;

public class MaxPlayerPerIP extends Special implements Listeners {
	
	public MaxPlayerPerIP() {
		super(SpecialKeys.MAX_PLAYER_PER_IP, false);
	}
	
	@EventListener
	public void onConnect(LoginEvent e) {
		if(!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		int currentOnIP = NegativityPlayer.getAllPlayers().values().stream().filter((np) -> np.getPlayer().isOnline() && np.getPlayer().getIP().equals(e.getAddress().getHostAddress()))
					.collect(Collectors.toList()).size();
		if(currentOnIP >= getConfig().getInt("number")) {
			e.setKickMessage(Messages.getMessage(NegativityAccount.get(e.getUUID()), "kick.kicked", "%name%", "Negativity", "%reason%", getName()));
			e.setLoginResult(Result.KICK_BANNED);
		}
	}
}
