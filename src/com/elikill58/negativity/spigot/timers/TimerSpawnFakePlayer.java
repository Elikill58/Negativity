package com.elikill58.negativity.spigot.timers;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;

public class TimerSpawnFakePlayer extends BukkitRunnable {

	@Override
	public void run() {
		if(Cheat.forKey("FORCEFIELD").get().isActive())
			return;
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
			np.saveProof();
			np.makeAppearEntities();
		}
	}

}
