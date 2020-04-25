package com.elikill58.negativity.spigot.timers;

import java.util.ArrayList;

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
			for(PlayerCheatAlertEvent alert : new ArrayList<>(np.getAlertForAllCheat()))
				SpigotNegativity.sendAlertMessage(np, alert);
			np.saveProof();
		}
	}

}
