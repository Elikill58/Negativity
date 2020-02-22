package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.NegativityAccount;

public class ActualizeClickTimer extends BukkitRunnable {

	@Override
	public void run() {
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			NegativityAccount account = np.getAccount();
			if (account.getMostClicksPerSecond() < np.ACTUAL_CLICK) {
				account.setMostClicksPerSecond(np.ACTUAL_CLICK);
			}
			np.LAST_CLICK = np.ACTUAL_CLICK;
			np.ACTUAL_CLICK = 0;
			if (np.SEC_ACTIVE < 2) {
				np.SEC_ACTIVE++;
				return;
			}
		}
	}

}
