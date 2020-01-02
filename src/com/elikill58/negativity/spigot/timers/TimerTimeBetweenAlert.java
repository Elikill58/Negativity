package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.permissions.Perm;

public class TimerTimeBetweenAlert extends BukkitRunnable {

	@Override
	public void run() {
		for(Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			int ping = Utils.getPing(p);
			for (Player pl : Utils.getOnlinePlayers()) {
				if (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pl), "showAlert")) {
					np.ALERT_NOT_SHOWED.forEach((c, i) -> {
						if(i.size() == 0)
							return;
						String hover = c.getHoverFor(np);
						if(hover != "")
							hover = "\n" + hover;
						if(i.size() == 1) {
							SpigotNegativity.sendAlertMessage(i.get(0).getReportType(), np, p, c, ping, i.get(0).getReliability(), hover, i.get(0), false, i.get(0).getStatsSend());
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
							SpigotNegativity.sendAlertMessage(alert.getReportType(), np, p, c, ping, nb / i.size(), hover, alert, true, i.get(i.size() - 1).getStatsSend());
						}
					});
				}
			}
			np.ALERT_NOT_SHOWED.clear();
		}
	}

}
