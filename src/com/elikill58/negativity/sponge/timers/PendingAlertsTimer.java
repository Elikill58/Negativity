package com.elikill58.negativity.sponge.timers;

import java.util.Collection;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.SpongeNegativityPlayer;
import com.elikill58.negativity.sponge.listeners.PlayerCheatEvent;
import com.elikill58.negativity.sponge.utils.Utils;
import com.elikill58.negativity.universal.ReportType;

public class PendingAlertsTimer implements Runnable {

	@Override
	public void run() {
		Collection<Player> onlinePlayers = Utils.getOnlinePlayers();
		for (Player player : onlinePlayers) {
			SpongeNegativityPlayer nPlayer = SpongeNegativityPlayer.getNegativityPlayer(player);
			nPlayer.pendingAlerts.forEach((cheat, alerts) -> {
				if (alerts.isEmpty()) {
					return;
				}

				int ping = Utils.getPing(player);
				String hover = cheat.getHoverFor(nPlayer);
				if (!hover.isEmpty()) {
					hover = "\n" + hover;
				}

				if (alerts.size() == 1) {
					PlayerCheatEvent.Alert alert = alerts.get(0);
					SpongeNegativity.sendAlertMessage(alert.getReportType(), player, cheat,
							alert.getReliability(), hover, nPlayer, ping, alert, false);
				} else {
					PlayerCheatEvent.Alert referenceAlert = null;
					int reliabilitySum = 0;
					for (PlayerCheatEvent.Alert alert : alerts) {
						reliabilitySum += alert.getReliability();
						if (alert.getReportType() == ReportType.VIOLATION) {
							referenceAlert = alert;
						}
					}

					if (referenceAlert == null) {
						referenceAlert = alerts.get(0);
					}

					SpongeNegativity.sendAlertMessage(referenceAlert.getReportType(), player, cheat,
							reliabilitySum / alerts.size(), hover, nPlayer, ping, referenceAlert, true);
				}
				alerts.clear();
			});
			nPlayer.pendingAlerts.clear();
		}
	}
}
