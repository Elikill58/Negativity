package com.elikill58.negativity.spigot.timers;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;

public class TimerTimeBetweenAlert extends BukkitRunnable {

	@Override
	public void run() {
		SpigotNegativityPlayer.getAllPlayers().forEach((uuid, np) -> {
			for(PlayerCheatAlertEvent alert : new ArrayList<>(np.getAlertForAllCheat()))
				SpigotNegativity.sendAlertMessage(np, alert);
			np.saveProof();
		});
	}

}
