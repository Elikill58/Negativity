package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.ClickableText;
import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
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
						if(i == 0)
							return;
						if(i == 1) {
							new ClickableText().addRunnableHoverEvent(
									Messages.getMessage(pl, "negativity.alert", "%name%", p.getName(), "%cheat%", c.getName(),
											"%reliability%", String.valueOf(100)),
									Messages.getMessage(pl, "negativity.alert_hover", "%reliability%",
											String.valueOf(100), "%ping%", String.valueOf(ping)),
									"/negativity " + p.getName()).sendToPlayer(pl);
						} else {
							new ClickableText().addRunnableHoverEvent(
									Messages.getMessage(pl, "negativity.alert_multiple", "%name%", p.getName(), "%cheat%", c.getName(),
											"%reliability%", String.valueOf(100), "%nb%", String.valueOf(i)),
									Messages.getMessage(pl, "negativity.alert_hover", "%reliability%",
											String.valueOf(100), "%ping%", String.valueOf(ping)),
									"/negativity " + p.getName()).sendToPlayer(pl);
						}
					});
				}
			}
			np.ALERT_NOT_SHOWED.clear();
		}
	}

}
