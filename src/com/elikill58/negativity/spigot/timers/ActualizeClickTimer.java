package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;

public class ActualizeClickTimer extends BukkitRunnable {
	
	@Override
	public void run() {
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			if(np.getAccount().getMostClicksPerSecond() < np.ACTUAL_CLICK)
				np.setBetterClick(np.ACTUAL_CLICK);
			np.LAST_CLICK = np.ACTUAL_CLICK;
			np.ACTUAL_CLICK = 0;
			if (np.SEC_ACTIVE < 2) {
				np.SEC_ACTIVE++;
				return;
			}
		}
	}

}
