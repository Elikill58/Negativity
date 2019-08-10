package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;

public class TimerSpawnFakePlayer extends BukkitRunnable {

	@Override
	public void run() {
		int i = 0;
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			i += np.proof.size();
			np.saveProof(false);
			np.makeAppearEntities();
		}
		Stats.updateStats(StatsType.CHEATS, i);
	}

}
