package com.elikill58.negativity.sponge.timers;

import java.util.ArrayList;
import java.util.Collection;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent.Alert;
import com.elikill58.negativity.sponge.utils.Utils;

public class PendingAlertsTimer implements Runnable {

	@Override
	public void run() {
		Collection<Player> onlinePlayers = Utils.getOnlinePlayers();
		for (Player player : onlinePlayers) {
			SpongeNegativityPlayer nPlayer = SpongeNegativityPlayer.getNegativityPlayer(player);
			for(Alert alert : new ArrayList<>(nPlayer.getAlertForAllCheat()))
				SpongeNegativity.sendAlertMessage(nPlayer, alert);
		}
	}
}
