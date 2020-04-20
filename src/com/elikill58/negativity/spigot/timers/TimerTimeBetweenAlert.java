package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.utils.Utils;

public class TimerTimeBetweenAlert extends BukkitRunnable {

	@Override
	public void run() {
		for(Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			for(PlayerCheatAlertEvent alert : np.getAlertForAllCheat())
				SpigotNegativity.sendAlertMessage(np, alert, false);
			/*new HashMap<>(np.ALERT_NOT_SHOWED).forEach((c, i) -> {
				if(i.size() == 0)
					return;
				if(i.size() == 1) {
					PlayerCheatAlertEvent alert = i.get(0);
					SpigotNegativity.sendAlertMessage(alert.getReportType(), np, p, c, ping, alert.getReliability(), alert.getHoverProof(), alert, 1, alert.getStatsSend());
				} else {
					PlayerCheatAlertEvent alert = null;
					int nb = 0;
					for(PlayerCheatAlertEvent pca : i) {
						if(pca.getReportType().equals(ReportType.VIOLATION))
							alert = pca;
						nb += pca.getReliability();
					}
					if(alert == null)
						alert = i.get(0);
					SpigotNegativity.sendAlertMessage(alert.getReportType(), np, p, c, ping, nb / i.size(), alert.getHoverProof(), alert, i.size(), i.get(i.size() - 1).getStatsSend());
				}
			});*/
			np.saveProof();
		}
	}

}
